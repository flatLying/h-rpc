package top.dreamer.service.module.pipeline.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import top.dreamer.cache.core.HCache;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static top.dreamer.service.common.constants.CacheConstants.CLIENT_COMPLETABLE_FUTURE_CACHE;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 16:02
 */
public class SimpleClientInBoundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBufMsg = (ByteBuf) msg;
        String in = byteBufMsg.toString(StandardCharsets.UTF_8);
        HCache hCache = HrpcBootstrap.getInstance().getHCache();
        CompletableFuture future = hCache.get(CLIENT_COMPLETABLE_FUTURE_CACHE + 123L, CompletableFuture.class);
        if (future == null) {
            throw new HRpcBusinessException(String.format("客户端查询【%s】的future结果为空", "123L"));
        }
        future.complete(in);
    }
}
