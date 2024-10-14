package top.dreamer.service.module.message;

import top.dreamer.service.module.enums.HMessageType;
import top.dreamer.service.module.message.common.HHeader;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.response.HResponse;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 12:18
 * @description: 构建 message的工厂
 */
public class MessageFactory {

    public static HRequest HeartBeatRequest() {
        HHeader head = HHeader.builder().messageType(HMessageType.HEART_BEAT.getCode()).build();
        return HRequest.builder().header(head).build();
    }

    public static HResponse HeartBeatResponse(HRequest request) {
        HHeader head = request.getHeader();
        head.setFullLength(head.getHeaderLength() + Integer.BYTES);
        return HResponse.builder().header(head).build();
    }

    /**
     * 由于限流失败，直接返回
     * @return Fail Response
     */
    public static HResponse FailLimitResponse() {
        HHeader head = HHeader.builder().build();
        head.setFullLength(head.getHeaderLength() + Integer.BYTES);
        return HResponse.builder().header(head).code(500).build();
    }
}
