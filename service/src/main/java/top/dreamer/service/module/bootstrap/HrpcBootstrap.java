package top.dreamer.service.module.bootstrap;

import lombok.Getter;
import top.dreamer.cache.HCaffeine;
import top.dreamer.cache.core.HCache;
import top.dreamer.core.utils.NetUtils;
import top.dreamer.service.module.bootstrap.client_config.ReferenceConfig;
import top.dreamer.service.module.bootstrap.common_config.RegistryConfig;
import top.dreamer.service.module.bootstrap.server_config.ProtocolConfig;
import top.dreamer.service.module.bootstrap.server_config.ServiceConfig;
import top.dreamer.service.module.communication.HServer;
import top.dreamer.service.module.communication.impl.HServerImpl;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * 2. 启动netty
     */
    public void start() {
        registry();
        new HServerImpl().startServer();
    }


    /************************************************ SERVER PRIVATE的方法 ************************************************/
    /**
     * 把serviceConfigs这个set中的所有服务都注册到注册中心
     */
    private void registry() {
        String hostIp = NetUtils.getLocalIPAddress();
        // TODO: 暂时假设port为8080，后续改成netty端口
        String port = "8080";
        String hostIpAndPort = hostIp + ":" + port;

        for (ServiceConfig serviceConfig : this.serviceConfigs) {
            String methodName = serviceConfig.getServiceInterfaceClass().getName();
            this.registryConfig.getRegistry().createMethodNode(methodName);
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
     * 1. 把注册中心加载到 reference
     * 2. 从注册中心拉取该方法的 IP:PORT 列表
     * 3. 把这个列表传给 reference
     */
    public void connect() {
        this.referenceConfig.setRegistryConfig(this.registryConfig);
        List<InetSocketAddress> hosts = this.registryConfig.getRegistry().lookup(referenceConfig.getServiceInterfaceClass().getName());
        this.referenceConfig.setHosts(hosts);
    }

    /************************************************ CLIENT PRIVATE部分的方法 ************************************************/

}
