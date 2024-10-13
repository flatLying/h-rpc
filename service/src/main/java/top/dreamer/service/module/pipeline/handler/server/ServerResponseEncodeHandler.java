package top.dreamer.service.module.pipeline.handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import top.dreamer.service.common.utils.HMessageParser;
import top.dreamer.service.module.enums.HMessageType;
import top.dreamer.service.module.enums.HSerializeType;
import top.dreamer.service.module.message.common.HHeader;
import top.dreamer.service.module.message.response.HResponse;
import top.dreamer.service.module.processor.serializer.HSerializer;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 21:23
 * @description: 把HResponse 序列化 + 压缩 ===> ByteBuf
 */
@Slf4j
public class ServerResponseEncodeHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        HResponse hResponse = (HResponse) msg;
        HHeader header = hResponse.getHeader();
        ByteBuf headerBuf = HMessageParser.encodeHHeader(header);
        headerBuf.writeInt(hResponse.getCode());
        // 如果是心跳响应报文
        if (header.getMessageType() == HMessageType.HEART_BEAT.getCode()) {
            ctx.writeAndFlush(headerBuf, promise);
            return;
        }

        // 不同报文
        HSerializer serializer = HSerializeType.getByCode(header.getSerializeType()).getSerializer();
        byte[] payLoadBytes = serializer.serialize(hResponse.getPayLoad());
        headerBuf.writeBytes(payLoadBytes);
        ctx.writeAndFlush(headerBuf, promise);
    }
}
