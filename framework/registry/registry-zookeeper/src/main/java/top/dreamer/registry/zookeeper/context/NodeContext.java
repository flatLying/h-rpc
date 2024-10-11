package top.dreamer.registry.zookeeper.context;

import lombok.Builder;
import lombok.Data;
import org.apache.zookeeper.CreateMode;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 10:23
 * @description: zookeeper节点的上下文
 */
@Data
@Builder
public class NodeContext {
    /**
     * 节点的完整路径
     */
    private String path;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点上有的数据
     */
    private byte[] data;

    /**
     * 节点的属性：persistent / ephemeral
     */
    private CreateMode nodeMode;

}
