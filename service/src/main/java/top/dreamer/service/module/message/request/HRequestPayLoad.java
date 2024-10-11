package top.dreamer.service.module.message.request;

import lombok.Builder;
import lombok.Data;
import org.checkerframework.checker.units.qual.N;

import java.io.Serializable;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 17:27
 * @description: request的请求体
 */
@Data
@Builder
public class HRequestPayLoad implements Serializable {

    /**
     * 接口名字： top.dreamer.DemoProviderService
     */
    private String interfaceName;

    /**
     * 方法名字： sayHello
     */
    private String methodName;

    /**
     * 参数的class
     */
    private Class[] paramsClass;

    /**
     * 参数
     */
    private Object[] params;

    /**
     * 返回值class
     */
    private Class returnClass;
}
