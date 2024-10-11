package top.dreamer.service.module.bootstrap.server_config;

import lombok.Data;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 16:00
 * @description: 需要发布的服务的配置
 */
@Data
public class ServiceConfig<T> {
    /**
     * 接口interface的Clazz
     */
    private Class<T> serviceInterfaceClass;

    /**
     * 实现类
     */
    private T serviceImp;

    /**
     * 设置接口Clazz
     * @param serviceInterfaceClass 接口Clazz
     * @return INSTANCE
     */
    public ServiceConfig<T> setInterface(Class<T> serviceInterfaceClass) {
        this.serviceInterfaceClass = serviceInterfaceClass;
        return this;
    }

    /**
     * 设置实现类
     * @param serviceImp 实现类
     * @return INSTANCE
     */
    public ServiceConfig<T> setRef(T serviceImp) {
        this.serviceImp = serviceImp;
        return this;
    }
}
