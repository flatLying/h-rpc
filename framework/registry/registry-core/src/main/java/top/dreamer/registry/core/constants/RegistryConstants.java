package top.dreamer.registry.core.constants;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 17:16
 * @description: 注册中心用到的常量
 */
public interface RegistryConstants {
    /**
     * 默认的注册中心地址信息
     */
    String DEFAULT_ZOOKEEPER_CONNECTION = "127.0.0.1:2181";

    /**
     * 默认超时时间
     */
    Integer DEFAULT_TIME_OUT = 10 * 1000;

    /**
     * 服务端的持久路径
     */
    String SERVER_BASE_PERSISTENT_PATH = "/hrpc/server";

    /**
     * 客户端的持久路径
     */
    String CLIENT_BASE_PERSISTENT_PATH = "/hrpc/client";
}
