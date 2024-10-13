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

}
