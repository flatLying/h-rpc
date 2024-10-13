package top.dreamer.service.common.constants;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 15:45
 * @description: 缓存相关的常量
 */
public interface CacheConstants {
    /**
     * 缓存注册中心的主机的cache
     */
    String HOST_CACHE = "HOST_CACHE";

    /**
     * 缓存客户端使用的completableFuture的cache
     */
    String CLIENT_COMPLETABLE_FUTURE_CACHE = "CLIENT_COMPLETABLE_FUTURE_CACHE";

    /**
     * 缓存服务端的serviceConfig的cache
     */
    String SERVER_SERVICE_CONFIG_CACHE = "SERVER_SERVICE_CONFIG_CACHE";
}
