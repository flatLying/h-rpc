package top.dreamer.service.module.detector.event;

import lombok.Getter;

import java.util.EventObject;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 18:49
 * @description: 修改Event
 */
public class UpdateEvent extends EventObject {

    @Getter
    private String path;

    @Getter
    private byte[] data;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public UpdateEvent(Object source, String path, byte[] data) {
        super(source);
        this.path = path;
        this.data = data;
    }
}
