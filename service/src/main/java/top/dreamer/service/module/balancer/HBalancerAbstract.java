package top.dreamer.service.module.balancer;

import lombok.Getter;
import top.dreamer.registry.core.HRegistry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 15:46
 * @description: 模板实现
 */
public abstract class HBalancerAbstract implements HBalancer{

    /**
     * 服务名
     */
    protected String serviceName;

    /**
     * 所有物理主机信息
     */
    protected List<InetSocketAddress> hosts;

    @Override
    public List<InetSocketAddress> getHosts() {
        return hosts;
    }

    protected HRegistry registry;

    public HBalancerAbstract(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void setRegistryCenter(HRegistry registryCenter) {
        this.registry = registryCenter;
    }

    @Override
    public HRegistry getRegistryCenter() {
        return registry;
    }
}
