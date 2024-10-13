package top.dreamer.service.common.context;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import top.dreamer.service.module.message.common.HHeader;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.request.HRequestPayLoad;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:39
 * @description: request的上下文对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HRequestContext {
    /**
     * 消息内容
     */
    private HRequest request;

    /**
     * 请求头
     */
    private HHeader header;

    /**
     * 消息载荷内容
     */
    private HRequestPayLoad requestPayLoad;

    /**
     * 整个消息的bytes
     */
    private byte[] requestBytes;

    /**
     * payLoad 的bytes
     */
    private byte[] payLoadBytes;
}
