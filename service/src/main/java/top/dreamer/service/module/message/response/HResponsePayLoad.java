package top.dreamer.service.module.message.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 14:09
 * @description: 响应负载
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HResponsePayLoad implements Serializable {

    /**
     * 响应payLoad
     */
    private Object result;

    /**
     * 返回值类型Clazz
     */
    private Class clazz;
}
