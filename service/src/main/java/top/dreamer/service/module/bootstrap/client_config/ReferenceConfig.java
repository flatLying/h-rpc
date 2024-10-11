package top.dreamer.service.module.bootstrap.client_config;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import top.dreamer.cache.core.HCache;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.bootstrap.common_config.RegistryConfig;
import top.dreamer.service.module.communication.impl.HClientImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static top.dreamer.service.common.constants.CacheConstants.CLIENT_COMPLETABLE_FUTURE_CACHE;
import static top.dreamer.service.common.constants.CacheConstants.HOST_CACHE;
import static top.dreamer.service.common.constants.HRpcConstants.RESPONSE_WAIT_TIME;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 16:15
 * @description: Client调用服务配置
 */
public class ReferenceConfig<T> {

    /**
     * hosts列表
     */
    @Setter
    private List<InetSocketAddress> hosts;


    /**
     * 调用方法接口的Clazz
     */
    @Getter
    private Class<T> serviceInterfaceClass;

    /**
     * 拉取服务的registryConfig
     */
    @Setter
    private RegistryConfig registryConfig;

    /**
     * 配置需要调用方法接口的Clazz
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

    class ClientProxyHandler implements InvocationHandler {

        /**
         * 1. 从hosts列表中选择一个进行连接（有相关的策略）
         *  a. Cache中没有Channel则重新连接并缓存
         * 2. 创建一个hRpcId绑定的completableFuture，然后发送消息出去
         *  a. 异步：客户端的入栈接收器当接收到这个hRpcId对于的结果时候，把结果放回completableFuture
         *  b. 同步：阻塞在方法的结尾，等待completableFuture接收到返回值
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            InetSocketAddress host = hosts.get(0);
            Channel channel = getChannel(host);
            // TODO：hRpcId生成暂时没有实现
            String hRpcId = "123456";
            HCache hCache = HrpcBootstrap.getInstance().getHCache();
            hCache.put(CLIENT_COMPLETABLE_FUTURE_CACHE + hRpcId, new CompletableFuture<>());
            channel.writeAndFlush(Unpooled.copiedBuffer("I'm client, hello server!", StandardCharsets.UTF_8));
            try {
                return hCache.get(CLIENT_COMPLETABLE_FUTURE_CACHE + hRpcId, CompletableFuture.class).get(RESPONSE_WAIT_TIME, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new HRpcBusinessException("未能在限定时间内得到RPC服务器端相应");
            }
        }

        /**
         * 获取一个Channel
         *  缓存 or 重新连接
         * @param host 主机信息
         * @return Channel
         */
        private Channel getChannel(InetSocketAddress host) {
            Channel channel = HrpcBootstrap.getInstance().getHCache().get(HOST_CACHE + host.toString(), Channel.class);
            if (channel == null) {
                channel = new HClientImpl().connect(host.getHostString(), host.getPort());
                HrpcBootstrap.getInstance().getHCache().put(HOST_CACHE + host, channel);
            }
            return channel;
        }
    }
}
