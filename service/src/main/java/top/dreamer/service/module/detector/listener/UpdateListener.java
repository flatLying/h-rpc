package top.dreamer.service.module.detector.listener;

import top.dreamer.service.module.detector.event.UpdateEvent;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 18:52
 * @description: 更新listener
 */
public interface UpdateListener {

    void updateEvent(UpdateEvent updateEvent);
}
