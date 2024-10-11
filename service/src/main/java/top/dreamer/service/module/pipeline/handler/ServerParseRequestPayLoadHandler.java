package top.dreamer.service.module.pipeline.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import top.dreamer.service.common.context.HRequestContext;
import top.dreamer.service.module.enums.HCompressType;
import top.dreamer.service.module.enums.HSerializeType;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.request.HRequestPayLoad;
import top.dreamer.service.module.processor.compressor.HCompressor;

import java.nio.charset.StandardCharsets;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 19:04
 * @description: 解压缩，解序列化得到完整的报文
 */
public class ServerParseRequestPayLoadHandler extends SimpleChannelInboundHandler<HRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HRequest msg) throws Exception {
        HCompressor compressor = HCompressType.getByCode(msg.getCompressType()).getCompressor();
        byte[] payLoadBytes = compressor.decompress(msg.getHRequestPayLoad());
        HRequestPayLoad payLoad = HSerializeType.getByCode(msg.getSerializeType()).getSerializer().deserialize(payLoadBytes, HRequestPayLoad.class);
        HRequestContext context = HRequestContext.builder()
                .requestPayLoad(payLoad)
                .request(msg).build();

        // 测试：将参数返回给客户端
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer(context.getRequestPayLoad().getInterfaceName(), StandardCharsets.UTF_8));
//        ctx.fireChannelRead(context);
    }
}
