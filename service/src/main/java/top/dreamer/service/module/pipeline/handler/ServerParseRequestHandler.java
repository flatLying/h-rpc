package top.dreamer.service.module.pipeline.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import top.dreamer.service.common.constants.MessageConstants;
import top.dreamer.service.common.utils.HRequestParser;
import top.dreamer.service.module.message.request.HRequest;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:46
 * @description: 服务器端用于还原出原来的 request
 */
public class ServerParseRequestHandler extends LengthFieldBasedFrameDecoder {

    public ServerParseRequestHandler() {
        super(MessageConstants.MAX_FRAME_LEN, MessageConstants.LENGTH_FIELD_OFFSET, MessageConstants.LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return HRequestParser.decode(in);
    }
}
