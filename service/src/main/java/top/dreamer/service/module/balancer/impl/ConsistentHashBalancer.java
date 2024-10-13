package top.dreamer.service.module.balancer.impl;

import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.balancer.HBalancerAbstract;
import top.dreamer.service.module.detector.heartbeat.HeartBeatDetector;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 16:25
 * @description: 一致性Hash负载均衡
 */
public class ConsistentHashBalancer extends HBalancerAbstract {

    /**
     * 虚拟节点的数量
     */
    private static final int VIRTUAL_NODE_COUNT = 100;

    /**
     * 哈希环，用于存储节点
     */
    private final TreeMap<Long, InetSocketAddress> hashRing = new TreeMap<>();


    public ConsistentHashBalancer(String serviceName) {
        super(serviceName);
    }

    @Override
    public void balance() {
        this.hosts = HeartBeatDetector.records.get(serviceName).keySet().stream().toList();
        if (this.hosts.isEmpty()) {
            throw new HRpcBusinessException("当前没有拉取到可用的服务节点");
        }

        // 清空旧的哈希环
        hashRing.clear();

        // 将每个物理节点的虚拟节点加入哈希环
        for (InetSocketAddress host : hosts) {
            for (int i = 0; i < VIRTUAL_NODE_COUNT; i++) {
                long hash = hash(generateKey(host, i));
                hashRing.put(hash, host);
            }
        }
    }

    @Override
    public InetSocketAddress next() {
        throw new HRpcBusinessException("一致性Hash均衡算法不支持该方法");
    }

    @Override
    public InetSocketAddress next(Object key) {
        initHost();
        // 请求的某个特征值计算哈希
        long requestHash = hash(key.toString());

        // 找到大于等于请求哈希值的第一个节点
        Map.Entry<Long, InetSocketAddress> entry = hashRing.ceilingEntry(requestHash);

        // 如果没有找到，则回到哈希环的第一个节点
        if (entry == null) {
            entry = hashRing.firstEntry();
        }

        return entry.getValue();
    }

    /**
     * 初始化主机列表
     */
    private void initHost() {
        if (this.hosts == null || this.hosts.isEmpty()) {
            balance();
            if (this.hosts == null || this.hosts.isEmpty()) {
                throw new HRpcBusinessException("当前没有拉取到可用的服务节点");
            }
        }
    }

    /**
     * 生成虚拟节点的键
     * @param host 主机信息
     * @param replicaIndex 虚拟节点的索引
     * @return 生成的节点键
     */
    private String generateKey(InetSocketAddress host, int replicaIndex) {
        return host.toString() + "##VN" + replicaIndex;
    }

    /**
     * 对字符串进行哈希计算
     * @param key 键
     * @return 哈希值
     */
    private long hash(String key) {
        return md5Hash(key);
    }

    /**
     * 使用 MD5 计算哈希值
     * @param key 要计算的字符串
     * @return 哈希值
     */
    private long md5Hash(String key) {
        try {
            byte[] digest = java.security.MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8));
            // 将前8个字节转为long类型
            return ((long) (digest[7] & 0xFF) << 56)
                    | ((long) (digest[6] & 0xFF) << 48)
                    | ((long) (digest[5] & 0xFF) << 40)
                    | ((long) (digest[4] & 0xFF) << 32)
                    | ((long) (digest[3] & 0xFF) << 24)
                    | ((long) (digest[2] & 0xFF) << 16)
                    | ((long) (digest[1] & 0xFF) << 8)
                    | (digest[0] & 0xFF);
        } catch (Exception e) {
            throw new HRpcBusinessException("MD5计算哈希失败");
        }
    }
}
