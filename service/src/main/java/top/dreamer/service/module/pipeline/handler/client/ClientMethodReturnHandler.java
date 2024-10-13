package top.dreamer.service.module.pipeline.handler.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.dreamer.cache.core.HCache;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.enums.HMessageType;
import top.dreamer.service.module.message.response.HResponse;
import java.util.concurrent.CompletableFuture;
import static top.dreamer.service.common.constants.CacheConstants.CLIENT_COMPLETABLE_FUTURE_CACHE;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 14:17
 * @description: 客户端处理return
 */
public class ClientMethodReturnHandler extends SimpleChannelInboundHandler<HResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HResponse msg) throws Exception {
        long hRpcId = msg.getHeader().getMessageId();
        HCache hCache = HrpcBootstrap.getInstance().getHCache();
        CompletableFuture future = hCache.get(CLIENT_COMPLETABLE_FUTURE_CACHE + hRpcId, CompletableFuture.class);
        if (future == null) {
            throw new HRpcBusinessException(String.format("客户端查询【%s】的future结果为空", hRpcId));
        }
        // 如果是心跳消息，则把全部信息写回
        if (HMessageType.HEART_BEAT.getCode() == msg.getHeader().getMessageType()) {
            future.complete(msg);
        }
        // 正常消息
        else {
            future.complete(msg.getPayLoad().getClazz().cast(msg.getPayLoad().getResult()));
        }
    }
}
