package top.dreamer.service.module.detector.listener;

import top.dreamer.service.module.detector.event.OfflineEvent;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 18:50
 * @description: 下线
 */
public interface OfflineListener {

    void offLineEvent(OfflineEvent event);

}
