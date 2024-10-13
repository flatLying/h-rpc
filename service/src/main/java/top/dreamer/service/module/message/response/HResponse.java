package top.dreamer.service.module.message.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.dreamer.service.module.message.common.HHeader;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 16:34
 * @description: 相应消息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HResponse {

    /**
     * 响应头
     */
    private HHeader header;

    /**
     * 响应码
     */
    @Builder.Default
    private int code = 200;

    /**
     * 响应负载
     */
    private HResponsePayLoad payLoad;
}
