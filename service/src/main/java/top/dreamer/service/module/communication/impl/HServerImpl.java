package top.dreamer.service.module.communication.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.common.config.HRpcDefaultConfiguration;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.communication.HServer;
import top.dreamer.service.module.pipeline.initializer.ServerChannelInitializer;

import java.net.InetSocketAddress;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 11:37
 * @description: 服务器端实现
 */
@Slf4j
public class HServerImpl implements HServer {
    @Override
    public InetSocketAddress startServer() {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            ChannelFuture channelFuture = serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer())
                    .bind(new HRpcDefaultConfiguration().getHost(), new HRpcDefaultConfiguration().getPort()).sync();
            log.info("服务器端Netty成功启动，启动地址【{}】", channelFuture.channel().localAddress());
            InetSocketAddress address = (InetSocketAddress) channelFuture.channel().localAddress();
            // 阻塞线程避免退出（用新的线程来阻塞）
            new Thread(() -> {
                try {
                    channelFuture.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    log.error("服务器Netty启动失败");
                    throw new HRpcBusinessException("服务器Netty启动失败");
                } finally {
                    boss.shutdownGracefully();
                    worker.shutdownGracefully();
                }
            });
            return address;
        } catch (InterruptedException e) {
            log.error("服务器Netty启动失败");
            throw new HRpcBusinessException("服务器Netty启动失败");
        }
    }
}
