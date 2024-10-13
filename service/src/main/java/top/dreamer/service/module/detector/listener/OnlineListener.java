package top.dreamer.service.module.detector.listener;

import top.dreamer.service.module.detector.event.OnlineEvent;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 18:40
 * @description: 上线监听
 */
public interface OnlineListener {

    void onLineEvent(OnlineEvent event);
}
