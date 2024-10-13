package top.dreamer.service.module.balancer.impl;

import lombok.extern.slf4j.Slf4j;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.balancer.HBalancerAbstract;
import top.dreamer.service.module.detector.heartbeat.HeartBeatDetector;

import java.net.InetSocketAddress;
import java.util.Map;

import static top.dreamer.service.common.constants.HRpcConstants.HEARTBEAT_DETECT_INTERVAL;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 13:58
 * @description: 最短响应时间balancer
 */
@Slf4j
public class MinimumResponseTimeBalancer extends HBalancerAbstract {

    public MinimumResponseTimeBalancer(String serviceName) {
        super(serviceName);
    }

    @Override
    public void balance() {

    }


    @Override
    public InetSocketAddress next() {
        Map<InetSocketAddress, Long> responseTimeMap = HeartBeatDetector.records.get(serviceName);
        if (responseTimeMap == null || responseTimeMap.isEmpty()) {
            try {
                Thread.sleep(HEARTBEAT_DETECT_INTERVAL * 1500);
            } catch (InterruptedException e) {
                throw new HRpcBusinessException("最短响应时间balancer出错");
            }
            responseTimeMap = HeartBeatDetector.records.get(serviceName);
        }
        if (responseTimeMap == null || responseTimeMap.isEmpty()) {
            throw new HRpcBusinessException("最短响应时间balancer没有找到可用的节点");
        }
        return responseTimeMap.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(() -> new HRpcBusinessException("最短响应时间balancer没有找到可用的节点"))
                .getKey();
    }

    @Override
    public InetSocketAddress next(Object key) {
        return next();
    }
}
