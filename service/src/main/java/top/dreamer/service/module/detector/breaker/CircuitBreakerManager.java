package top.dreamer.service.module.detector.breaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-14 17:30
 * @description: 熔断器管理中心
 */
public class CircuitBreakerManager {

    /**
     * 存储服务名对应的熔断器
     */
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    /**
     * 获取服务对应的熔断器，如果不存在则创建
     * @param serviceName 服务名
     * @param failureThreshold 熔断阈值
     * @param openStateTimeoutMillis 熔断时间
     * @param halfOpenRequestLimit 半开状态测试请求上限
     * @return 返回服务对应的熔断器
     */
    public CircuitBreaker getCircuitBreaker(String serviceName, int failureThreshold, long openStateTimeoutMillis, int halfOpenRequestLimit) {
        return circuitBreakers.computeIfAbsent(serviceName, name ->
                new CircuitBreaker(failureThreshold, openStateTimeoutMillis, halfOpenRequestLimit));
    }

    /**
     * 获取服务对应的熔断器
     * @param serviceName 服务名
     * @return 如果存在，返回熔断器；否则返回null
     */
    public CircuitBreaker getCircuitBreaker(String serviceName) {
        return circuitBreakers.get(serviceName);
    }
}
