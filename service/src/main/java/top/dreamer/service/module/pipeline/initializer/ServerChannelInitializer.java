package top.dreamer.service.module.pipeline.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.dreamer.service.module.pipeline.handler.server.*;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 11:45
 * @description: 初始化channel
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 解析 frame 中的报文，
        // byBuf --> HRequestContext
        ch.pipeline().addLast(new ServerParseRequestHandler());
        // 响应其中的心跳 不改变消息类型
        ch.pipeline().addLast(new ServerHeartBeatHandler());
        // 解析得到payLoad(方法名、参数、返回值......)
        // HRequestContext --> HRequest
        ch.pipeline().addLast(new ServerParseRequestPayLoadHandler());
        // 执行方法获取返回值，得到响应报文
        // HRequest --> HResponse
        ch.pipeline().addLast(new ServerMethodInvokeHandler());

        // 出栈：将HResponse转为ByteBuf
        // HResponse --> BytBuf
        ch.pipeline().addLast(new ServerResponseEncodeHandler());
    }
}
