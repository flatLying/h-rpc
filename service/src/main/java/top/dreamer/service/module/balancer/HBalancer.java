package top.dreamer.service.module.balancer;

import top.dreamer.registry.core.HRegistry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 15:32
 * @description: 负载均衡器的接口
 */
public interface HBalancer {
    /**
     * 配置注册中心
     */
    void setRegistryCenter(HRegistry registryCenter);

    /**
     * 获取注册中心
     */
    HRegistry getRegistryCenter();

    /**
     * 重新初始化一次（动态上下线）
     */
    void balance();

    /**
     * 获取连接的IP和端口
     * @return IP和端口
     */
    InetSocketAddress next();

    /**
     * 传入一个特征值进行next
     * @param key 特征值
     * @return IP和端口
     */
    InetSocketAddress next(Object key);

    /**
     * 获取hosts
     */
    List<InetSocketAddress> getHosts();

    /**
     * 设置服务名
     */
    void setServiceName(String serviceName);
}
