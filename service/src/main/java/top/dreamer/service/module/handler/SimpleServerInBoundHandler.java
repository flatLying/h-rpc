package top.dreamer.service.module.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 14:47
 * @description: TODO
 */
@Slf4j
public class SimpleServerInBoundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBufMsg = (ByteBuf) msg;
        String in = byteBufMsg.toString(StandardCharsets.UTF_8);
        log.info("服务器端接收到消息：{}", in);
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("func_tool:" + in, StandardCharsets.UTF_8));
    }
}
