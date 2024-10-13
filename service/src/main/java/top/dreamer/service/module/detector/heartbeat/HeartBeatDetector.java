package top.dreamer.service.module.detector.heartbeat;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.dreamer.cache.core.HCache;
import top.dreamer.schedule.HScheduleTaskAbstract;
import top.dreamer.service.common.utils.HMessageParser;
import top.dreamer.service.module.balancer.HBalancer;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.detector.event.OfflineEvent;
import top.dreamer.service.module.detector.event.OnlineEvent;
import top.dreamer.service.module.detector.event.UpdateEvent;
import top.dreamer.service.module.detector.listener.OfflineListener;
import top.dreamer.service.module.detector.listener.OnlineListener;
import top.dreamer.service.module.detector.listener.UpdateListener;
import top.dreamer.service.module.message.request.HRequest;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import static top.dreamer.service.common.constants.CacheConstants.CLIENT_COMPLETABLE_FUTURE_CACHE;
import static top.dreamer.service.common.constants.HRpcConstants.HEARTBEAT_DETECT_INTERVAL;


/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 11:04
 * @description: 心跳检测
 */
@Slf4j
public class HeartBeatDetector implements OnlineListener, OfflineListener, UpdateListener {

    /**
     * 心跳检测结果
     */
    public static Map<String, ConcurrentHashMap<InetSocketAddress, Long>> records = new HashMap<>();

    /**
     * 负载均衡器
     */
    @Getter
    private HBalancer balancer;

    private HeartBeatDetector() {}

    private static volatile HeartBeatDetector INSTANCE;

    public static HeartBeatDetector getInstance() {
        if (INSTANCE == null) {
            synchronized (HeartBeatDetector.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HeartBeatDetector();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @param balancer 均衡器
     * @param serviceName serviceName
     */
    public static void detectHearBeat(String serviceName, HBalancer balancer) {
        HeartBeatDetector.getInstance().balancer = balancer;
        List<InetSocketAddress> hosts = balancer.getRegistryCenter().lookup(serviceName);
        for (InetSocketAddress host : hosts) {
            HeartBeatDetectTask task = new HeartBeatDetectTask(serviceName, host);
            task.scheduleTask(serviceName, 0, HEARTBEAT_DETECT_INTERVAL);
        }
    }

    /**
     * 把request发送到channel
     * @param request 请求
     * @param channel channel
     */
    public static void sendRequest2Channel(HRequest request, Channel channel) {
        HCache hCache = HrpcBootstrap.getInstance().getHCache();
        hCache.put(CLIENT_COMPLETABLE_FUTURE_CACHE + request.getHeader().getMessageId(), new CompletableFuture<>());
        channel.writeAndFlush(HMessageParser.encodeHRequest(request));
    }


    /**
     * 记录心跳结果
     * @param serviceName 服务名
     * @param address 地址
     * @param interval 间隔
     */
    public static void updateHeartBeat(String serviceName, InetSocketAddress address, long interval) {
        ConcurrentHashMap<InetSocketAddress, Long> hostIntervals = records.computeIfAbsent(serviceName, k -> new ConcurrentHashMap<>());
        hostIntervals.put(address, interval);
    }

    /**
     * 删除下线的节点
     * @param serviceName 服务名
     * @param address 地址
     */
    public static void removeHost(String serviceName, InetSocketAddress address) {
        records.get(serviceName).remove(address);
    }

    /**
     * 打印结果
     */
    public static void logHeartBeatIntervals(String serviceName) {
        ConcurrentHashMap<InetSocketAddress, Long> map = HeartBeatDetector.records.get(serviceName);
        if (map == null || map.isEmpty()) {
            return;
        }
        log.info("============== HEART BEAT INTERVALS START ==============");
        for (Map.Entry<InetSocketAddress, Long> entry : map.entrySet()) {
            log.info("主机【{}】---- 响应时间【{}】", entry.getKey(), entry.getValue());
        }
        log.info("============== HEART BEAT INTERVALS END ==============");
    }

    /**
     * path转serviceName
     * @param path hrpc/server/top.dreamer.com.Greet/127.0.0.1:8080
     * @return top.dreamer.com.Greet
     */
    private String path2ServiceName(String path) {
        int lastSlashIndex = path.lastIndexOf('/');
        int secondLastSlashIndex = path.lastIndexOf('/', lastSlashIndex - 1);
        return path.substring(secondLastSlashIndex + 1, lastSlashIndex);
    }

    /**
     * 监听上线的事件，重新拉取一遍hosts
     * @param event 上线的事件
     */
    @Override
    public void onLineEvent(OnlineEvent event) {
        if (HeartBeatDetector.getInstance().balancer != null) {
            String path = event.getPath();
            String serviceName = path2ServiceName(path);
            log.info("【{}】检测到节点上线【{}】【{}】",serviceName, path, event.getData());
            HScheduleTaskAbstract.stopTask(serviceName);
            HeartBeatDetector.detectHearBeat(serviceName, HeartBeatDetector.getInstance().getBalancer());
        }
    }

    @Override
    public void offLineEvent(OfflineEvent event) {
        if (HeartBeatDetector.getInstance().balancer != null) {
            String path = event.getPath();
            String serviceName = path2ServiceName(path);
            log.info("【{}】检测到节点下线【{}】【{}】", serviceName, path, event.getData());
            HScheduleTaskAbstract.stopTask(serviceName);
            HeartBeatDetector.detectHearBeat(serviceName, HeartBeatDetector.getInstance().getBalancer());
        }
    }

    @Override
    public void updateEvent(UpdateEvent updateEvent) {
        log.info("检测到节点更新【{}】【{}】", updateEvent.getPath(), updateEvent.getData());
    }
}

