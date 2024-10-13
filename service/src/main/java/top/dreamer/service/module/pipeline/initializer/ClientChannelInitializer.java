package top.dreamer.service.module.pipeline.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import top.dreamer.service.module.pipeline.handler.client.ClientMethodReturnHandler;
import top.dreamer.service.module.pipeline.handler.client.ClientResponseDecoderHandler;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 11:54
 * @description: 客户端初始化Channel
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LoggingHandler());
        // byte --> HResponse
        ch.pipeline().addLast(new ClientResponseDecoderHandler());
        // 处理得到的return返回值
        ch.pipeline().addLast(new ClientMethodReturnHandler());
    }
}
