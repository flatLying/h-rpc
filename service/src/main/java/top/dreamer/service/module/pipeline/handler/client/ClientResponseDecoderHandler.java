package top.dreamer.service.module.pipeline.handler.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import top.dreamer.service.common.constants.MessageConstants;
import top.dreamer.service.common.utils.HMessageParser;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 13:55
 * @description: 还原Response
 */
public class ClientResponseDecoderHandler extends LengthFieldBasedFrameDecoder {
    public ClientResponseDecoderHandler() {
        super(MessageConstants.MAX_FRAME_LEN, MessageConstants.LENGTH_FIELD_OFFSET, MessageConstants.LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return HMessageParser.decodeHResponse(in);
    }
}
