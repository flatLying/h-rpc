package top.dreamer.service.module.bootstrap.client_config;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import top.dreamer.cache.core.HCache;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.common.config.HRpcDefaultConfiguration;
import top.dreamer.service.common.constants.HRpcConstants;
import top.dreamer.service.common.utils.HMessageParser;
import top.dreamer.service.common.utils.IdGenerator;
import top.dreamer.service.module.balancer.HBalancer;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.communication.impl.HClientImpl;
import top.dreamer.service.module.detector.breaker.CircuitBreaker;
import top.dreamer.service.module.detector.breaker.CircuitBreakerManager;
import top.dreamer.service.module.detector.heartbeat.HeartBeatDetector;
import top.dreamer.service.module.enums.HCompressType;
import top.dreamer.service.module.enums.HMessageType;
import top.dreamer.service.module.enums.HSerializeType;
import top.dreamer.service.module.message.common.HHeader;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.request.HRequestPayLoad;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static top.dreamer.service.common.constants.CacheConstants.CLIENT_COMPLETABLE_FUTURE_CACHE;
import static top.dreamer.service.common.constants.CacheConstants.HOST_CACHE;
import static top.dreamer.service.common.constants.HRpcConstants.RESPONSE_WAIT_TIME;
import static top.dreamer.service.common.constants.MessageConstants.HEADER_LENGTH;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 16:15
 * @description: Client调用服务配置
 */
@Slf4j
public class ReferenceConfig<T> {

    /**
     * 均衡器
     */
    @Setter
    @Getter
    private HBalancer balancer;

    /**
     * 熔断器
     */
    private final CircuitBreakerManager circuitBreakerManager = new CircuitBreakerManager();


    /**
     * 调用方法接口的Clazz
     */
    @Getter
    private Class<T> serviceInterfaceClass;

    /**
     * 序列化方式
     */
    @Setter
    @Getter
    private HSerializeType serializeType = new HRpcDefaultConfiguration().getSerializer();

    /**
     * 压缩方式
     */
    @Setter
    @Getter
    private HCompressType compressType = new HRpcDefaultConfiguration().getCompressor();

    /**
     * ID生成器
     */
    private IdGenerator idGenerator = new IdGenerator();

    /**
     * 1. 配置需要调用方法接口的Clazz
     * 2. 初始化心跳检测配置
     * @param serviceInterfaceClass 调用方法接口的Clazz
     * @return INSTANCE
     */
    public ReferenceConfig<T> setInterface(Class<T> serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
        return this;
    }

    /**
     * 得到一个代理对象
     * @return 代理
     */
    public T get() {
        return serviceInterfaceClass.cast(Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{this.serviceInterfaceClass},
                new ClientProxyHandler()
        ));
    }

    /**
     * 获取一个Channel
     *  缓存 or 重新连接
     * @param host 主机信息
     * @return Channel
     */
    public static Channel getChannel(InetSocketAddress host) {
        Channel channel = HrpcBootstrap.getInstance().getHCache().get(HOST_CACHE + host.toString(), Channel.class);
        if (channel == null) {
            channel = new HClientImpl().connect(host.getHostString(), host.getPort());
            HrpcBootstrap.getInstance().getHCache().put(HOST_CACHE + host, channel);
        }
        return channel;
    }

    /**
     * 开始进行心跳检测
     * 1. 查询出所有的Channel
     * 2. 利用心跳检测器对于所有的Channel进行检测
     */
    public void startHeartBeatDetect() {
        HeartBeatDetector.detectHearBeat(serviceInterfaceClass.getName(), balancer);
    }

    class ClientProxyHandler implements InvocationHandler {

        /**
         * 先进行熔断器过滤
         *
         * 1. 从hosts列表中选择一个进行连接（有相关的策略）
         *  a. Cache中没有Channel则重新连接并缓存
         * 2. 创建一个hRpcId绑定的completableFuture，然后发送消息出去
         *  a. 异步：客户端的入栈接收器当接收到这个hRpcId对于的结果时候，把结果放回completableFuture
         *  b. 同步：阻塞在方法的结尾，等待completableFuture接收到返回值
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args){
            CircuitBreaker circuitBreaker = circuitBreakerManager.getCircuitBreaker(serviceInterfaceClass.getName(), HRpcConstants.FAILURE_THREAD, HRpcConstants.OPEN_STATE_TIMEOUT, HRpcConstants.HALF_OPEN_LIMITER);
            if (!circuitBreaker.allowRequest()) {
                throw new HRpcBusinessException(String.format("当前【】服务被熔断", serviceInterfaceClass.getName()));
            }

            HRequest request = getRequest(method, args);
            sendRequest(request);
            try {
                HCache hCache = HrpcBootstrap.getInstance().getHCache();
                Object result = hCache.get(CLIENT_COMPLETABLE_FUTURE_CACHE + request.getHeader().getMessageId(), CompletableFuture.class).get(RESPONSE_WAIT_TIME, TimeUnit.SECONDS);
                circuitBreaker.onSuccess();
                return result;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error(e.getMessage(), e);
                circuitBreaker.onFailure();
                throw new HRpcBusinessException("未能在限定时间内得到RPC服务器端相应");
            }
        }

        /**
         * 发送消息
         * @param request 消息
         */
        private void sendRequest(HRequest request) {
            HCache hCache = HrpcBootstrap.getInstance().getHCache();
            hCache.put(CLIENT_COMPLETABLE_FUTURE_CACHE + request.getHeader().getMessageId(), new CompletableFuture<>());

            InetSocketAddress host = balancer.next(request.getHeader().getMessageId());
            Channel channel = getChannel(host);
            channel.writeAndFlush(HMessageParser.encodeHRequest(request));
        }

        /**
         * 1. 获取payLoad
         * 2. 获取请求头
         * 3. 获取完整请求
         *
         * @param method  方法
         * @param args    参数
         */
        private HRequest getRequest(Method method, Object[] args) {

            HRequestPayLoad payLoad = getRequestPayLoad(method, args);
            byte[] payLoadBytes = serializeType.getSerializer().serialize(payLoad);
            payLoadBytes = compressType.getCompressor().compress(payLoadBytes);

            HHeader hHeader = getHHeader(payLoadBytes);

            return HRequest.builder().
                    header(hHeader).hRequestPayLoad(payLoad).build();
        }


        /**
         * 获取请求头
         * @return HHeader
         */
        private HHeader getHHeader(byte[] payLoadBytes) {
            long hRpcId = idGenerator.nextId();
            return HHeader.builder()
                    .fullLength(HEADER_LENGTH + payLoadBytes.length)
                    .compressType(compressType.getCode())
                    .serializeType(serializeType.getCode())
                    .messageType(HMessageType.NORMAL.getCode())
                    .messageId(hRpcId)
                    .build();
        }

        /**
         * 获取requestPayLoad
         * @param method 方法
         * @param args 参数
         * @return requestPayLoad
         */
        private HRequestPayLoad getRequestPayLoad(Method method, Object[] args) {
            return HRequestPayLoad.builder().interfaceName(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .paramsClass(method.getParameterTypes())
                    .params(args)
                    .returnClass(method.getReturnType()).build();
        }
    }
}
