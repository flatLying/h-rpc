package top.dreamer.service.module.communication;

import io.netty.channel.Channel;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 11:35
 * @description: 客户端
 */
public interface HClient {
    /**
     * 连接服务器
     */
    Channel connect(String host, int port);
}
