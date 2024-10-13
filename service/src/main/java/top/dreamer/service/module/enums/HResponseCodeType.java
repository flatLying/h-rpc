package top.dreamer.service.module.enums;

import lombok.Data;
import lombok.Getter;
import top.dreamer.core.exception.HRpcBusinessException;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 13:38
 * @description: 响应code
 */
@Getter
public enum HResponseCodeType {

    SUCCESS(200, "成功"),
    FAIL(500, "失败");

    /**
     * code
     */
    private int code;

    /**
     * 描述
     */
    private String desc;

    HResponseCodeType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public HResponseCodeType getByCode(int code) {
        for (HResponseCodeType type : HResponseCodeType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new HRpcBusinessException("响应code出错");
    }
}
