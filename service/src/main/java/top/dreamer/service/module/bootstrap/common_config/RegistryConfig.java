package top.dreamer.service.module.bootstrap.common_config;

import lombok.Data;
import top.dreamer.registry.core.HRegistry;
import top.dreamer.registry.zookeeper.HZookeeperRegistry;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 15:51
 * @description: 注册中心配置内容
 */
@Data
public class RegistryConfig {
    /**
     * 配置的注册中心
     */
    private HRegistry registry;

    /**
     * 构造注册中心
     * 1. 解析协议
     * 2. 构造连接注册中心
     * @param registryString 协议全名称
     */
    public RegistryConfig(String registryString) {
        // TODO： 解析协议 Nacos还是Zookeeper，暂时默认Zookeeper
        String registryType = registryString.substring(0, registryString.indexOf("://"));
        String hostIpAndPort = registryString.substring(registryString.indexOf("://") + 3);
        this.registry = new HZookeeperRegistry(hostIpAndPort);
    }

}
