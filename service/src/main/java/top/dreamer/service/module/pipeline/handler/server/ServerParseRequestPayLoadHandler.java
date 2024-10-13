package top.dreamer.service.module.pipeline.handler.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.dreamer.service.common.context.HRequestContext;
import top.dreamer.service.module.enums.HCompressType;
import top.dreamer.service.module.enums.HSerializeType;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.request.HRequestPayLoad;
import top.dreamer.service.module.processor.compressor.HCompressor;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 19:04
 * @description: 解压缩，解序列化得到完整的报文
 */
public class ServerParseRequestPayLoadHandler extends SimpleChannelInboundHandler<HRequestContext> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HRequestContext msg) throws Exception {
        HCompressor compressor = HCompressType.getByCode(msg.getHeader().getCompressType()).getCompressor();
        byte[] payLoadBytes = compressor.decompress(msg.getPayLoadBytes());
        HRequestPayLoad payLoad = HSerializeType.getByCode(msg.getHeader().getSerializeType()).getSerializer().deserialize(payLoadBytes, HRequestPayLoad.class);
        HRequest hRequest = HRequest.builder().hRequestPayLoad(payLoad).header(msg.getHeader()).build();
        ctx.fireChannelRead(hRequest);
    }
}
