package top.dreamer.service.module.message.common;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import top.dreamer.service.common.utils.IdGenerator;
import top.dreamer.service.module.enums.HCompressType;
import top.dreamer.service.module.enums.HMessageType;
import top.dreamer.service.module.enums.HSerializeType;

import static top.dreamer.service.common.constants.MessageConstants.*;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 10:52
 * @description: 公共响应头内容
 */
@Data
@Builder
public class HHeader {
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
    @Default
    private byte compressType = HCompressType.GZIP.getCode();

    /**
     * 序列化类型
     */
    @Default
    private byte serializeType = HSerializeType.FAST_JSON.getCode();

    /**
     * 消息类型
     */
    @Default
    private byte messageType = HMessageType.NORMAL.getCode();

    /**
     * 消息ID
     */
    @Default
    private long messageId = new IdGenerator().nextId();

    /**
     * 时间戳
     */
    @Default
    private long timeStamp = System.currentTimeMillis();

}
