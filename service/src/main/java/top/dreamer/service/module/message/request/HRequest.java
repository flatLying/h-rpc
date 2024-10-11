package top.dreamer.service.module.message.request;

import lombok.Builder;
import lombok.Data;

import lombok.Builder.Default;

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
     * 魔术值 常数
     */
    @Default
    private byte[] magic = MAGIC;

    /**
     * 版本号 常数
     */
    @Default
    private byte version = VERSION;

    /**
     * 请求头长度 常数
     */
    @Default
    private short headerLength = HEADER_LENGTH;

    /**
     * 全部消息长度
     */
    private int fullLength;

    /**
     * 压缩类型
     */
    private byte compressType;

    /**
     * 序列化类型
     */
    private byte serializeType;

    /**
     * 消息类型
     */
    private byte messageType;

    /**
     * 消息ID
     */
    private long messageId;

    /**
     * 消息载荷
     */
    private byte[] hRequestPayLoad;
}
