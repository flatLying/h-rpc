package top.dreamer.service.common.constants;

import java.nio.charset.StandardCharsets;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 16:36
 * @description: 和消息相关的常量
 */
public interface MessageConstants {
    /**
     * 魔术值头
     */
    byte[] MAGIC = "hrpc".getBytes(StandardCharsets.UTF_8);

    /**
     * 版本version
     */
    byte VERSION = 1;

    /**
     * 消息头长度字段: 30 常数
     */
    short HEADER_LENGTH = (short) (MAGIC.length + 1 + Short.BYTES + Integer.BYTES + 3 + Long.BYTES + Long.BYTES);

    /**
     * 定义的最大帧的长度
     */
    int MAX_FRAME_LEN = 1024 * 1024;

    /**
     * 长度字段的偏移
     */
    int LENGTH_FIELD_OFFSET = 7;

    /**
     * 长度字段长度
     */
    int LENGTH_FIELD_LENGTH = 4;
}
