package top.dreamer.service.module.detector.breaker;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-14 17:31
 */
public class RpcClient {

    private final CircuitBreakerManager circuitBreakerManager = new CircuitBreakerManager();

    public void callRemoteService(String serviceName) {
        // 获取服务对应的熔断器，假设失败阈值为5次，熔断时间为3000毫秒，半开状态测试请求上限为3次
        CircuitBreaker circuitBreaker = circuitBreakerManager.getCircuitBreaker(serviceName, 5, 3000, 3);

        if (circuitBreaker.allowRequest()) {
            try {
                // 模拟调用远程服务
                System.out.println("Calling service: " + serviceName);
                boolean success = simulateRemoteServiceCall(serviceName);

                if (success) {
                    circuitBreaker.onSuccess();
                    System.out.println("Service " + serviceName + " call succeeded.");
                } else {
                    circuitBreaker.onFailure();
                    System.out.println("Service " + serviceName + " call failed.");
                }
            } catch (Exception e) {
                circuitBreaker.onFailure();
                System.out.println("Exception occurred when calling service: " + serviceName + ", error: " + e.getMessage());
            }
        } else {
            System.out.println("Request to service " + serviceName + " is blocked due to circuit breaker being in OPEN state.");
        }
    }

    // 模拟远程服务调用
    private boolean simulateRemoteServiceCall(String serviceName) throws Exception {
        // 模拟服务调用的随机成功/失败
        double random = Math.random();
        if (random < 0.1) {
            return true;  // 70% 成功
        } else if (random < 0.9) {
            return false; // 20% 失败
        } else {
            throw new Exception("Service unavailable"); // 10% 抛出异常
        }
    }

    public static void main(String[] args) {
        RpcClient client = new RpcClient();

        // 模拟对同一个服务进行多次调用
        for (int i = 0; i < 100; i++) {
            client.callRemoteService("ServiceA");
        }

        // 模拟对另一个服务的调用
        for (int i = 0; i < 100; i++) {
            client.callRemoteService("ServiceB");
        }
    }
}
