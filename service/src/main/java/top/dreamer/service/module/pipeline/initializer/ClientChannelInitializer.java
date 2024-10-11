package top.dreamer.service.module.pipeline.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.dreamer.service.module.pipeline.handler.SimpleClientInBoundHandler;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 11:54
 * @description: 客户端初始化Channel
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new SimpleClientInBoundHandler());
    }
}
