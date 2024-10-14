package top.dreamer.service.common.constants;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 15:55
 * @description: HRpc相关的常量
 */
public interface HRpcConstants {
    /**
     * 正常调用等待响应时间 秒
     */
    Integer RESPONSE_WAIT_TIME = 10;

    /**
     * 心跳检测等待响应时间 秒
     */
    Integer HEARTBEAT_WAIT_TIME = 4;

    /**
     *心跳检测间隔
     */
    Integer HEARTBEAT_DETECT_INTERVAL = 5;

    /**
     * 心跳断线重试次数
     */
    Integer HEARTBEAT_RETRY_NUM = 3;

    /**
     * 心跳断线重试间隔时间 MS
     */
    Integer HEARTBEAT_RETRY_INTERVAL = 500;

    /**
     * 令牌桶定时任务的KEY
     */
    String TOKEN_BUCKET_KEY = "TOKEN_BUCKET_KEY";

    /**
     * 令牌桶初始容量
     */
    int TOKEN_BUCKET_INIT_CAPACITY = 100;

    /**
     * 令牌桶添加速率
     */
    int TOKEN_BUCKET_ADD_RATE = 10;

    /**
     * 熔断器的熔断阈值，多少次失败后熔断
     */
    int FAILURE_THREAD = 5;

    /**
     * 熔断后等待的时间
     */
    long OPEN_STATE_TIMEOUT = 3000;

    /**
     * 半打开状态下，允许放行多少请求测试
     */
    int HALF_OPEN_LIMITER = 3;
}
