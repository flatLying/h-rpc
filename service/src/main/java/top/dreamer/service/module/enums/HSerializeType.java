package top.dreamer.service.module.enums;

import lombok.Getter;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.processor.serializer.HSerializer;
import top.dreamer.service.module.processor.serializer.impl.FastJsonSerializer;
import top.dreamer.service.module.processor.serializer.impl.JavaSerializer;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 16:33
 * @description: 序列化方式
 */
@Getter
public enum HSerializeType {

    FAST_JSON((byte) 1, "FAST_JSON", new FastJsonSerializer()),
    JAVA((byte) 2, "JAVA", new JavaSerializer()),;

    /**
     * 编码
     */
    private byte code;

    /**
     * 描述
     */
    private String desc;

    /**
     * 序列化器
     */
    private HSerializer serializer;

    HSerializeType(byte code, String desc, HSerializer serializer) {
        this.code = code;
        this.desc = desc;
        this.serializer = serializer;
    }

    public static HSerializeType getByCode(byte code) {
        for (HSerializeType type : HSerializeType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new HRpcBusinessException("序列化code出错");
    }
}
