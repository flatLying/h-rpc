package top.dreamer.service.module.communication.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.communication.HClient;
import top.dreamer.service.module.pipeline.initializer.ClientChannelInitializer;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 11:38
 * @description: 客户端实现
 */
public class HClientImpl implements HClient {

    private static final NioEventLoopGroup group = new NioEventLoopGroup();
    private static final Logger log = LoggerFactory.getLogger(HClientImpl.class);

    /**
     * 连接服务器
     * 加入Channel到缓存
     * @param host 服务器IP
     * @param port 端口
     */
    @Override
    public Channel connect(String host, int port) {
        try {
            ChannelFuture channelFuture = new Bootstrap().group(HClientImpl.group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())
                    .connect(host, port).sync();
            if (channelFuture.isSuccess()) {
                log.info("客户端建立连接成功【{}】", host + ":" + port);
                return channelFuture.channel();
            } else {
                throw new HRpcBusinessException("客户端建立连接失败");
            }
        } catch (InterruptedException e) {
            throw new HRpcBusinessException("客户端建立连接失败");
        }
    }
}
