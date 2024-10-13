package top.dreamer.service.module.message.request;

import lombok.Builder;
import lombok.Data;

import lombok.Builder.Default;
import top.dreamer.service.module.message.common.HHeader;

import static top.dreamer.service.common.constants.MessageConstants.*;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 16:33
 * @description: 请求消息
 */
@Builder
@Data
public class HRequest {

    /**
     * 请求头
     */
    private HHeader header;

    /**
     * 消息载荷
     */
    private HRequestPayLoad hRequestPayLoad;
}
