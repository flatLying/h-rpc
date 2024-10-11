package top.dreamer.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Op;
import org.apache.zookeeper.data.Stat;
import org.w3c.dom.Node;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.core.exception.HRpcFrameworkException;
import top.dreamer.core.utils.NetUtils;
import top.dreamer.registry.core.HRegistryAbstract;
import top.dreamer.registry.zookeeper.context.NodeContext;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static top.dreamer.registry.core.constants.RegistryConstants.*;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-10 17:36
 * @description: ZooKeeper的封装
 */
@Slf4j
public class HZookeeperRegistry extends HRegistryAbstract {
    /**
     * zookeeper连接
     */
    private CuratorFramework zk;

    /**
     * 空参构造
     */
    public HZookeeperRegistry() {
        this(null, null);
    }

    /**
     * 指定connectString构造
     * @param connectString 连接地址
     */
    public HZookeeperRegistry(String connectString) {
        this(connectString, null);
    }

    /**
     * 1. 连接Zookeeper
     * 2. 创建初始化的/hrpc/server  /hrpc/client节点
     * @param connectString 连接地址
     * @param timeOut timeOut
     */
    public HZookeeperRegistry(String connectString, Integer timeOut) {
        this.zk = CuratorFrameworkFactory.newClient(
                connectString == null ? DEFAULT_ZOOKEEPER_CONNECTION : connectString,
                timeOut == null ? DEFAULT_TIME_OUT : timeOut,
                timeOut == null ? DEFAULT_TIME_OUT : timeOut,
                new ExponentialBackoffRetry(1000, 3)
        );
        try {
            this.zk.start();
            init();
        } catch (Exception e) {
            throw new HRpcFrameworkException("Zookeeper注册中心连接初始化失败："  + e.getMessage());
        }
    }

    /**
     * 在项目启动时，先在zookeeper上创建好根节点等信息
     */
    public void init() {
        NodeContext serverRootNode = NodeContext.builder().path(SERVER_BASE_PERSISTENT_PATH).nodeMode(CreateMode.PERSISTENT).build();
        NodeContext clientRootNode = NodeContext.builder().path(CLIENT_BASE_PERSISTENT_PATH).nodeMode(CreateMode.PERSISTENT).build();
        createNode(serverRootNode);
        createNode(clientRootNode);
        log.info("Zookeeper 注册中心初始化完成");
    }

    /**
     * 创建方法节点
     * @param methodName methodName
     */
    @Override
    public void createMethodNode(String methodName) {
        String path = SERVER_BASE_PERSISTENT_PATH + "/" + methodName;
        NodeContext node = NodeContext.builder().path(path).nodeMode(CreateMode.PERSISTENT).build();
        createNode(node);
    }

    /**
     * 创建主机的节点，并且附带上主机信息
     * @param methodName 方法名
     * @param hostIpAndPort "127.0.0.1:8080"
     * @param hostInfo 主机信息
     */

    @Override
    public void createHostNode(String methodName, String hostIpAndPort, String hostInfo) {
        String path = SERVER_BASE_PERSISTENT_PATH + "/" + methodName + "/" + hostIpAndPort;
        NodeContext node = NodeContext.builder().path(path).nodeMode(CreateMode.EPHEMERAL).build();
        createNode(node);
    }

    @Override
    public List<InetSocketAddress> lookup(String methodName) {
        String path = SERVER_BASE_PERSISTENT_PATH + "/" + methodName;
        List<NodeContext> hostsInfo = getChildren(path);
        return hostsInfo.stream()
                .map(context -> NetUtils.parseInetAddress(context.getNodeName()))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    /******************************************* private *******************************************/


    /**
     * 创建持久节点
     * 如果不存在则创建一个
     *
     * @param nodeContext 需要创建节点的上下文内容
     */
    public void createNode(NodeContext nodeContext) {
        try {
            Stat stat = this.zk.checkExists().forPath(nodeContext.getPath());
            if (stat == null) {
                if (nodeContext.getData() == null) {
                    this.zk.create()
                            .creatingParentsIfNeeded()
                            .withMode(nodeContext.getNodeMode())
                            .forPath(nodeContext.getPath());
                } else {
                    this.zk.create()
                            .creatingParentsIfNeeded()
                            .withMode(nodeContext.getNodeMode())
                            .forPath(nodeContext.getPath(), nodeContext.getData());
                }
            }
        } catch (Exception e) {
            throw new HRpcFrameworkException(String.format("Zookeeper创建持久节点【%s】失败): ", nodeContext.getPath()) + e);
        }
    }

    /**
     * 获取直接子节点的所有信息，包括节点名和data byte[]
     * @param path 路径
     * @return 所有子节点的信息
     */
    private List<NodeContext> getChildren(String path) {
        try {
            List<String> childrenName = this.zk.getChildren().forPath(path);
            return childrenName.stream().map(childName -> getNode(path + "/" + childName)).collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new HRpcBusinessException(String.format("获取【%s】的子节点信息失败", path));
        }
    }

    /**
     * 通过路径path获取完整的node信息
     * @param path 路径path
     * @return 完整的node信息
     */
    private NodeContext getNode(String path) {
        try {
            byte[] data = this.zk.getData().forPath(path);
            String nodeName = path.substring(path.lastIndexOf("/") + 1);
            return NodeContext.builder().path(path).data(data).nodeName(nodeName).build();
        } catch (Exception e) {
            throw new HRpcBusinessException(String.format("获取节点【%s】信息失败", path));
        }
    }



    public static void main(String[] args) {
        HZookeeperRegistry hZookeeperRegistry = new HZookeeperRegistry("127.0.0.1:2181");
        hZookeeperRegistry.init();
    }
}
