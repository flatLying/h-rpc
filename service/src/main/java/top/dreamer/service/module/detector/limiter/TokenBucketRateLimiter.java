package top.dreamer.service.module.detector.limiter;

import top.dreamer.schedule.HScheduleTaskAbstract;
import top.dreamer.service.common.constants.HRpcConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-14 16:55
 * @description: 令牌桶限流器，支持多令牌桶配置，动态调整速率，统计信息功能。
 */
public class TokenBucketRateLimiter {

    /**
     * 令牌桶的存储，支持多个服务或接口独立的令牌桶配置
     */
    private static final Map<String, TokenBucket> bucketMap = new ConcurrentHashMap<>();

    /**
     * 创建或获取现有的令牌桶
     * @param serviceKey 服务/接口的唯一标识
     * @param bucketCapacity 令牌桶容量
     * @param tokenRate 令牌生成速率
     */
    public static void createOrUpdateBucket(String serviceKey, int bucketCapacity, int tokenRate) {
        bucketMap.compute(serviceKey, (key, existingBucket) -> {
            if (existingBucket == null) {
                return new TokenBucket(bucketCapacity, tokenRate);
            } else {
                existingBucket.updateBucket(bucketCapacity, tokenRate);
                return existingBucket;
            }
        });
    }

    /**
     * 获取令牌桶并尝试获取执行权限
     * @param serviceKey 服务/接口的唯一标识
     * @param tokens 消耗的token数量
     * @return 是否允许执行
     */
    public static boolean tryAcquire(String serviceKey, int tokens) {
        TokenBucket bucket = bucketMap.get(serviceKey);
        if (bucket != null) {
            return bucket.tryAcquire(tokens);
        } else {
            System.out.println("No token bucket found for service: " + serviceKey);
            return false;
        }
    }

    /**
     * 关闭指定服务/接口的令牌桶
     * @param serviceKey 服务/接口的唯一标识
     */
    public static void shutdownBucket(String serviceKey) {
        TokenBucket bucket = bucketMap.get(serviceKey);
        if (bucket != null) {
            bucket.shutdown();
            bucketMap.remove(serviceKey);
        }
    }

    /**
     * 令牌桶类，内部实现令牌的生成与消耗逻辑
     */
    static class TokenBucket {

        /**
         * 令牌桶数量
         */
        private int bucketCapacity;
        /**
         * 生成令牌速率
         */
        private int tokenRate;
        /**
         * 当前桶中的数量
         */
        private volatile int currentTokens;
        /**
         * 成功请求的计数
         */
        private volatile long successRequests = 0;
        /**
         * 被拒绝的请求计数
         */
        private volatile long rejectedRequests = 0;
        /**
         * 确保线程安全的锁，同时只有一个线程进行获取token，避免并发错误
         */
        private final ReentrantLock lock = new ReentrantLock();

        class AddTokenTask extends HScheduleTaskAbstract {
            @Override
            public void executeScheduledTask() {
                addTokens();
            }
        }

        /**
         * 设置初始容量
         * 开启定时往桶中添加令牌的定时任务
         * @param bucketCapacity
         * @param tokenRate
         */
        public TokenBucket(int bucketCapacity, int tokenRate) {
            this.bucketCapacity = bucketCapacity;
            this.tokenRate = tokenRate;
            this.currentTokens = bucketCapacity; // 初始令牌数设为满桶
            new AddTokenTask().scheduleTask(HRpcConstants.TOKEN_BUCKET_KEY, 0, 1);
        }

        /**
         * 更新令牌桶的容量和速率
         * @param bucketCapacity 新的桶容量
         * @param tokenRate 新的令牌生成速率
         */
        public void updateBucket(int bucketCapacity, int tokenRate) {
            this.bucketCapacity = bucketCapacity;
            this.tokenRate = tokenRate;
            System.out.println("Updated bucket capacity to " + bucketCapacity + " and token rate to " + tokenRate);
        }

        /**
         * 往桶中添加token
         */
        private void addTokens() {
            lock.lock();
            try {
                if (currentTokens < bucketCapacity) {
                    currentTokens = Math.min(bucketCapacity, currentTokens + tokenRate);
                    System.out.println("Added tokens, current token count: " + currentTokens);
                }
            } finally {
                lock.unlock();
            }
        }

        /**
         * 尝试获取执行权限
         * @param tokens 消耗的token数量
         * @return 是否允许执行
         */
        public boolean tryAcquire(int tokens) {
            lock.lock();
            try {
                if (currentTokens >= tokens) {
                    currentTokens -= tokens;
                    successRequests++;
                    System.out.println("Acquired tokens, remaining: " + currentTokens);
                    return true;
                } else {
                    rejectedRequests++;
                    System.out.println("Not enough tokens, current: " + currentTokens);
                    return false;
                }
            } finally {
                lock.unlock();
            }
        }

        /**
         * 获取统计信息
         * @return 返回统计信息，包含成功请求数和拒绝请求数
         */
        public String getStatistics() {
            return "Success requests: " + successRequests + ", Rejected requests: " + rejectedRequests;
        }

        /**
         * 关闭添加token的定时任务
         */
        public void shutdown() {
            HScheduleTaskAbstract.stopTask(HRpcConstants.TOKEN_BUCKET_KEY);
        }
    }
}
