package top.dreamer.service.module.pipeline.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.common.context.HRequestContext;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.bootstrap.server_config.ServiceConfig;
import top.dreamer.service.module.enums.HMessageType;
import top.dreamer.service.module.enums.HResponseCodeType;
import top.dreamer.service.module.enums.HSerializeType;
import top.dreamer.service.module.message.common.HHeader;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.request.HRequestPayLoad;
import top.dreamer.service.module.message.response.HResponse;
import top.dreamer.service.module.message.response.HResponsePayLoad;
import top.dreamer.service.module.processor.serializer.HSerializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static top.dreamer.service.common.constants.CacheConstants.SERVER_SERVICE_CONFIG_CACHE;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 20:52
 * @description: 服务器端invoke调用impl得到结果
 */
@Slf4j
public class ServerMethodInvokeHandler extends SimpleChannelInboundHandler<HRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HRequest msg) throws Exception {
        Object result = invokeMethod(msg.getHRequestPayLoad());
        log.info("服务器端执行调用方法【{}】，结果为【{}】", msg.getHRequestPayLoad().getMethodName(), result.toString());
        HResponse response = createResponse(result, msg);
        ctx.channel().writeAndFlush(response);
    }

    /**
     * 根据得到的响应封装一个响应报文
     * 1. 序列化result
     * 2. 设置请求头的 timeStamp和fullLength
     * 3. 封装HResponse
     * @param result 结果内容
     * @param msg HRequestContext
     */
    private HResponse createResponse(Object result, HRequest msg) {
        HResponsePayLoad responsePayLoad = HResponsePayLoad.builder().result(result)
                .clazz(msg.getHRequestPayLoad().getReturnClass()).build();

        HSerializer serializer = HSerializeType.getByCode(msg.getHeader().getSerializeType()).getSerializer();
        byte[] payLoadBytes = serializer.serialize(responsePayLoad);

        HHeader header = msg.getHeader();
        header.setTimeStamp(System.currentTimeMillis());
        header.setFullLength(header.getFullLength() + Integer.BYTES + payLoadBytes.length);


        return HResponse.builder().header(msg.getHeader())
                .code(HResponseCodeType.SUCCESS.getCode())
                .payLoad(responsePayLoad).build();
    }

    /**
     * 反射调用方法得到返回值结果
     * @param msg HRequestContext
     * @return 结果
     */
    private Object invokeMethod(HRequestPayLoad msg) {
        try {
            String serviceName = msg.getInterfaceName();
            String methodName = msg.getMethodName();
            Class[] paramsClass = msg.getParamsClass();
            ServiceConfig serviceConfig = HrpcBootstrap.getInstance().getHCache().get(SERVER_SERVICE_CONFIG_CACHE + serviceName, ServiceConfig.class);

            if (serviceConfig == null) {
                throw new HRpcBusinessException(String.format("服务器没有找到【%s】对应的实现类", serviceName));
            }
            Method method = serviceConfig.getServiceImp().getClass().getMethod(methodName, paramsClass);
            if (method == null) {
                throw new HRpcBusinessException(String.format("服务器没有找到【%s】对应的实现方法", methodName));
            }

            return method.invoke(serviceConfig.getServiceImp(), msg.getParams());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new HRpcBusinessException(String.format("服务器【%s, %s】调用失败",
                    msg.getInterfaceName().getClass().getName(),
                    msg.getMethodName()));
        }
    }
}
