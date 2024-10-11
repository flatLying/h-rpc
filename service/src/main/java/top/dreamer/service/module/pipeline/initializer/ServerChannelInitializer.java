package top.dreamer.service.module.pipeline.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.dreamer.service.module.pipeline.handler.ServerParseRequestHandler;
import top.dreamer.service.module.pipeline.handler.ServerParseRequestPayLoadHandler;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 11:45
 * @description: 初始化channel
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ServerParseRequestHandler());
        ch.pipeline().addLast(new ServerParseRequestPayLoadHandler());
    }
}
