package top.dreamer.service.module.detector.watcher;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import top.dreamer.service.module.detector.event.OfflineEvent;
import top.dreamer.service.module.detector.event.OnlineEvent;
import top.dreamer.service.module.detector.event.UpdateEvent;
import top.dreamer.service.module.detector.heartbeat.HeartBeatDetector;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 18:24
 * @description: 上下线的watcher
 */
@Slf4j
public class OnOfflineWatcher implements PathChildrenCacheListener {
    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        ChildData data = pathChildrenCacheEvent.getData();
        switch (pathChildrenCacheEvent.getType()) {
            case CHILD_ADDED:
                OnlineEvent onlineEvent = new OnlineEvent(this, data.getPath(), data.getData());
                HeartBeatDetector.getInstance().onLineEvent(onlineEvent);
                break;
            case CHILD_REMOVED:
                OfflineEvent offLineEvent = new OfflineEvent(this, data.getPath(), data.getData());
                HeartBeatDetector.getInstance().offLineEvent(offLineEvent);
                break;
            case CHILD_UPDATED:
                UpdateEvent updateEvent = new UpdateEvent(this, data.getPath(), data.getData());
                HeartBeatDetector.getInstance().updateEvent(updateEvent);
                break;
            default:
                break;
        }
    }
}
