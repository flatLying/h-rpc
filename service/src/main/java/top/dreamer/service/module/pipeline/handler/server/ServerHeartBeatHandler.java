package top.dreamer.service.module.pipeline.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.dreamer.service.common.context.HRequestContext;
import top.dreamer.service.module.enums.HMessageType;
import top.dreamer.service.module.message.MessageFactory;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.response.HResponse;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 20:46
 * @description: 响应心跳检测报文
 */
@Slf4j
public class ServerHeartBeatHandler extends SimpleChannelInboundHandler<HRequestContext> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HRequestContext msg) throws Exception {
        if (HMessageType.HEART_BEAT.getCode() == msg.getHeader().getMessageType()) {
            HResponse response = MessageFactory.HeartBeatResponse(msg.getRequest());
            ctx.channel().writeAndFlush(response);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
