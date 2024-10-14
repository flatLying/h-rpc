package top.dreamer.service.module.detector.breaker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-14 17:21
 * @description: 熔断器
 */
public class CircuitBreaker {

    /**
     * 熔断器状态
     */
    public enum State {
        CLOSED,     // 闭合状态：正常请求
        OPEN,       // 打开状态：熔断，拒绝所有请求
        HALF_OPEN   // 半开状态：允许部分请求进行测试
    }

    /**
     * 当前熔断器状态
     */
    private volatile State currentState = State.CLOSED;

    /**
     * 熔断阈值：失败多少次后熔断
     */
    private final int failureThreshold;
    /**
     * 半开状态下允许的测试请求数
     */
    private final int halfOpenRequestLimit;
    /**
     * 熔断后的等待时间（毫秒）
     */
    private final long openStateTimeoutMillis;
    /**
     * 熔断器上一次进入打开状态的时间
     */
    private long lastOpenTime = 0;

    /**
     * 失败次数统计
     */
    private final AtomicInteger failureCount = new AtomicInteger(0);
    /**
     * 成功次数统计（用于半开状态）
     */
    private final AtomicInteger successCount = new AtomicInteger(0);

    /**
     * 确保状态切换的线程安全锁
     */
    private final ReentrantLock stateLock = new ReentrantLock();

    public CircuitBreaker(int failureThreshold, long openStateTimeoutMillis, int halfOpenRequestLimit) {
        this.failureThreshold = failureThreshold;
        this.openStateTimeoutMillis = openStateTimeoutMillis;
        this.halfOpenRequestLimit = halfOpenRequestLimit;
    }

    /**
     * 执行熔断器
     * @return 是否允许操作
     */
    public boolean allowRequest() {
        stateLock.lock();
        try {
            // 如果是打开状态，检查是否超过了熔断时间，决定是否进入半开状态
            if (currentState == State.OPEN) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastOpenTime >= openStateTimeoutMillis) {
                    currentState = State.HALF_OPEN;
                    successCount.set(0);
                    System.out.println("Circuit Breaker moving to HALF_OPEN state.");
                } else {
                    return false; // 仍在熔断时间内，拒绝请求
                }
            }

            // 在半开状态时，限制请求数量
            if (currentState == State.HALF_OPEN && successCount.get() >= halfOpenRequestLimit) {
                return false; // 达到测试请求的上限
            }
            return true; // 允许请求
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * 请求成功时调用
     */
    public void onSuccess() {
        stateLock.lock();
        try {
            if (currentState == State.HALF_OPEN) {
                successCount.incrementAndGet();
                if (successCount.get() >= halfOpenRequestLimit) {
                    reset(); // 半开状态测试成功，恢复到闭合状态
                    System.out.println("Circuit Breaker moving to CLOSED state.");
                }
            } else {
                reset(); // 成功则复位失败计数
            }
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * 请求失败时调用
     */
    public void onFailure() {
        stateLock.lock();
        try {
            if (currentState == State.CLOSED) {
                int currentFailureCount = failureCount.incrementAndGet();
                if (currentFailureCount >= failureThreshold) {
                    trip(); // 达到失败阈值，熔断
                    System.out.println("Circuit Breaker moving to OPEN state.");
                }
            } else if (currentState == State.HALF_OPEN) {
                trip(); // 半开状态下请求失败，重新熔断
                System.out.println("Circuit Breaker moving back to OPEN state.");
            }
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * 熔断，进入打开状态
     */
    private void trip() {
        currentState = State.OPEN;
        lastOpenTime = System.currentTimeMillis();
        failureCount.set(0); // 重置失败计数
    }

    /**
     * 重置熔断器状态
     */
    private void reset() {
        currentState = State.CLOSED;
        failureCount.set(0); // 重置失败计数
    }

    /**
     * 获取当前状态
     * @return 当前状态
     */
    public State getState() {
        return currentState;
    }
}
