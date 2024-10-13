package top.dreamer.service.module.balancer.impl;

import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.balancer.HBalancerAbstract;
import top.dreamer.service.module.detector.heartbeat.HeartBeatDetector;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 15:39
 * @description: 轮询算法
 */
public class RoundRobinBalancer extends HBalancerAbstract {

    /**
     * 使用的节点的下标
     */
    private final AtomicInteger cursor = new AtomicInteger(0);

    public RoundRobinBalancer(String serviceName) {
        super(serviceName);
    }

    @Override
    public void balance() {
        this.hosts = HeartBeatDetector.records.get(serviceName).keySet().stream().toList();
    }

    @Override
    public InetSocketAddress next() {
        initHost();
        if (cursor.get() >= hosts.size()) {
            cursor.set(0);
        }
        return hosts.get(cursor.getAndIncrement());
    }

    @Override
    public InetSocketAddress next(Object key) {
        return next();
    }

    /**
     * 初始化拉取host信息
     */
    private void initHost() {
        if (this.hosts == null || this.hosts.isEmpty()) {
            balance();
            if (this.hosts == null || this.hosts.isEmpty()) {
                throw new HRpcBusinessException("当前没有拉取到可用的服务节点");
            }
        }
    }
}
