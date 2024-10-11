package top.dreamer.service.module.enums;

import lombok.Data;
import lombok.Getter;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.processor.compressor.HCompressor;
import top.dreamer.service.module.processor.compressor.impl.GzipCompressor;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 16:32
 * @description: 压缩类型
 */
@Getter
public enum HCompressType {
    GZIP((byte) 1, "GZIP", new GzipCompressor());

    /**
     * 压缩类型编码
     */
    private byte code;

    /**
     * 类型描述
     */
    private String desc;

    /**
     * 压缩器类
     */
    private HCompressor compressor;

    HCompressType(byte type, String desc, HCompressor compressor) {
        this.code = type;
        this.desc = desc;
        this.compressor = compressor;
    }

    public static HCompressType getByCode(byte code) {
        for (HCompressType type : HCompressType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new HRpcBusinessException("压缩code错误");
    }

}
