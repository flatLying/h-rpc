package top.dreamer.service.module.enums;

import lombok.Getter;
import top.dreamer.core.exception.HRpcBusinessException;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 16:33
 * @description: 消息类别（普通消息 / heartbeat）
 */
@Getter
public enum HMessageType {

    NORMAL((byte) 1, "NORMAL"),
    HEART_BEAT((byte) 2, "HEART_BEAT");

    private final byte code;

    private final String name;

    HMessageType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    private static HMessageType getByCode(byte code) {
        for (HMessageType value : HMessageType.values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new HRpcBusinessException("消息类别code出错");
    }
}
