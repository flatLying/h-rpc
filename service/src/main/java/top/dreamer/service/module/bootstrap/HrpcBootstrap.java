package top.dreamer.service.module.bootstrap;

import lombok.Getter;
import lombok.Setter;
import top.dreamer.cache.HCaffeine;
import top.dreamer.cache.core.HCache;
import top.dreamer.core.utils.NetUtils;
import top.dreamer.service.module.balancer.HBalancer;
import top.dreamer.service.module.balancer.impl.ConsistentHashBalancer;
import top.dreamer.service.module.balancer.impl.MinimumResponseTimeBalancer;
import top.dreamer.service.module.balancer.impl.RoundRobinBalancer;
import top.dreamer.service.module.bootstrap.client_config.ReferenceConfig;
import top.dreamer.service.module.bootstrap.common_config.RegistryConfig;
import top.dreamer.service.module.bootstrap.server_config.ProtocolConfig;
import top.dreamer.service.module.bootstrap.server_config.ServiceConfig;
import top.dreamer.service.module.communication.HServer;
import top.dreamer.service.module.communication.impl.HServerImpl;
import top.dreamer.service.module.detector.watcher.OnOfflineWatcher;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static top.dreamer.service.common.constants.CacheConstants.SERVER_SERVICE_CONFIG_CACHE;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 13:13
 * @description: RPC框架的启动引导类
 */
public class HrpcBootstrap {

    private HrpcBootstrap() {}


    private static final HrpcBootstrap INSTANCE = new HrpcBootstrap();

    /**
     * 应用名称
     */
    private String applicationName;


    /**
     * 缓存
     */
    @Getter
    private HCache hCache = new HCaffeine();

    /**
     * 负载均衡策略
     */
    @Setter
    @Getter
    private HBalancer balancer;

    /**
     * 单例模式，得到INSTANCE
     * @return 当前实例INSTANCE
     */
    public static HrpcBootstrap getInstance() {
        return INSTANCE;
    }


    /**
     * 客户端的reference
     */
    private ReferenceConfig<?> referenceConfig;

    /**
     * 注册中心配置类
     */
    private RegistryConfig registryConfig;

    /**
     * 服务Set
     */
    private final Set<ServiceConfig> serviceConfigs = new HashSet<>();


    /**
     * 设置名称
     * @param applicationName 名称
     * @return 当前实例INSTANCE
     */
    public HrpcBootstrap application(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    /**
     * 配置注册中心内容
     * @param registryConfig 注册中心配置
     * @return 当前实例INSTANCE
     */
    public HrpcBootstrap registry(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        return this;
    }

    /************************************************ SERVER PUBLIC的方法 ************************************************/

    /**
     * 配置传输协议
     * @param protocolConfig 传输协议配置
     * @return 当前实例INSTANCE
     */
    public HrpcBootstrap protocol(ProtocolConfig protocolConfig) {
        return this;
    }

    /**
     * 配置运行的服务
     * 1. 注册方法名 (persistent node)
     * 2. 注册当前主机的信息 ip:port (ephemeral node)
     * @param serviceConfig 运行服务配置
     * @return 当前实例INSTANCE
     */
    public <T> HrpcBootstrap service(ServiceConfig<T> serviceConfig) {
        this.serviceConfigs.add(serviceConfig);
        return this;
    }

    /**
     * 批量配置运行的服务
     * @param serviceConfigs 批量的服务配置
     * @return 当前实例INSTANCE
     */
    public <T> HrpcBootstrap service(List<ServiceConfig<T>> serviceConfigs) {
        serviceConfigs.forEach(this::service);
        return this;
    }

    /**
     * 1. 把hostIp, hostPort注册到Registry
     * 2. 把serviceConfig缓存到本地，后续可以调用
     * 3. 启动netty
     */
    public void start() {
        InetSocketAddress address = new HServerImpl().startServer();
        registry(address);
        saveServiceConfig();
    }



    /************************************************ SERVER PRIVATE的方法 ************************************************/

    /**
     * 把ServiceConfig缓存到本地，后续可以进行调用
     */
    private void saveServiceConfig() {
        for (ServiceConfig serviceConfig : serviceConfigs) {
            String serviceName = serviceConfig.getServiceInterfaceClass().getName();
            this.hCache.put(SERVER_SERVICE_CONFIG_CACHE + serviceName, serviceConfig);
        }
    }

    /**
     * 把serviceConfigs这个set中的所有服务都注册到注册中心
     */
    private void registry(InetSocketAddress address) {
        String hostIp = NetUtils.getLocalIPAddress();
        int port = address.getPort();
        String hostIpAndPort = hostIp + ":" + port;

        for (ServiceConfig serviceConfig : this.serviceConfigs) {
            String methodName = serviceConfig.getServiceInterfaceClass().getName();
            this.registryConfig.getRegistry().createMethodNode(methodName, new OnOfflineWatcher());
            this.registryConfig.getRegistry().createHostNode(methodName, hostIpAndPort, null);
        }
    }


    /************************************************ CLIENT PUBLIC部分的方法 ************************************************/

    /**
     * 设置客户端的reference
     * @param reference reference
     * @return 当前实例INSTANCE
     */
    public HrpcBootstrap reference(ReferenceConfig<?> reference) {
        this.referenceConfig = reference;
        return this;
    }

    /**
     * 创建注册中心监听器
     * 1. 把注册中心balancer
     * 2. 把balancer配置到reference
     * 3. 开始心跳检测
     */
    public void connect() {
        this.registryConfig.getRegistry().createMethodWatcher(this.referenceConfig.getServiceInterfaceClass().getName(), new OnOfflineWatcher());
        if (this.balancer == null) {
            this.balancer = new MinimumResponseTimeBalancer(this.referenceConfig.getServiceInterfaceClass().getName());
        }
        this.balancer.setRegistryCenter(this.registryConfig.getRegistry());
        this.referenceConfig.setBalancer(this.balancer);
        this.referenceConfig.startHeartBeatDetect();
    }

    /************************************************ CLIENT PRIVATE部分的方法 ************************************************/

}
