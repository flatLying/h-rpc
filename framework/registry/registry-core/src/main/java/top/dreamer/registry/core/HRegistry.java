package top.dreamer.registry.core;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 17:38
 * @description: 注册中心支持的方法
 */
public interface HRegistry {
    /**
     * 在注册中心创建方法节点
     * @param methodName 方法名
     */
    void createMethodNode(String methodName);

    /**
     * 在注册中心创建主机节点
     * @param methodName 方法名
     * @param hostIpAndPort "127.0.0.1:8080"
     * @param hostInfo 主机信息
     */
    void createHostNode(String methodName, String hostIpAndPort, String hostInfo);

    /**
     * 通过方法名查询可用的服务列表
     * @param methodName 方法名
     * @return 可用的服务列表
     */
    List<InetSocketAddress> lookup(String methodName);
}
