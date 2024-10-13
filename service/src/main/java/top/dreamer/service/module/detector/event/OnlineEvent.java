package top.dreamer.service.module.detector.event;

import jdk.jfr.Event;
import lombok.Getter;

import java.util.EventObject;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 18:37
 * @description: 上线
 */
public class OnlineEvent extends EventObject {

    /**
     * 路径名称
     */
    @Getter
    private String path;

    /**
     * 挂载的数据
     */
    @Getter
    private byte[] data;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public OnlineEvent(Object source, String path, byte[] data) {
        super(source);
        this.path = path;
        this.data = data;
    }

}
