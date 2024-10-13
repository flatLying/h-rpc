package top.dreamer.service.module.detector.heartbeat;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import top.dreamer.cache.core.HCache;
import top.dreamer.schedule.HScheduleTaskAbstract;
import top.dreamer.service.common.utils.HMessageParser;
import top.dreamer.service.module.bootstrap.HrpcBootstrap;
import top.dreamer.service.module.bootstrap.client_config.ReferenceConfig;
import top.dreamer.service.module.message.MessageFactory;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.response.HResponse;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

import static top.dreamer.service.common.constants.CacheConstants.CLIENT_COMPLETABLE_FUTURE_CACHE;
import static top.dreamer.service.common.constants.HRpcConstants.*;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-13 13:40
 * @description: 检测心跳任务
 */
@Slf4j
public class HeartBeatDetectTask extends HScheduleTaskAbstract {
    /**
     * 本次任务检测的地址
     */
    private final InetSocketAddress address;

    /**
     * 服务名
     */
    private final String serviceName;

    public HeartBeatDetectTask(String serviceName, InetSocketAddress address) {
        this.address = address;
        this.serviceName = serviceName;
    }

    @Override
    public void executeScheduledTask() {
        int retryNum = 1;
        while (retryNum <= HEARTBEAT_RETRY_NUM) {
            HRequest request = MessageFactory.HeartBeatRequest();
            Channel channel = ReferenceConfig.getChannel(this.address);
            channel.writeAndFlush(HMessageParser.encodeHRequest(request));
            HeartBeatDetector.sendRequest2Channel(request, channel);
            HCache hCache = HrpcBootstrap.getInstance().getHCache();
            CompletableFuture future = hCache.get(CLIENT_COMPLETABLE_FUTURE_CACHE + request.getHeader().getMessageId(), CompletableFuture.class);
            try {
                HResponse response = (HResponse) future.get(HEARTBEAT_WAIT_TIME, TimeUnit.SECONDS);
                long interval = response.getHeader().getTimeStamp() - request.getHeader().getTimeStamp();
                log.info("心跳检测【{}】--【{}】用时：【{}】", serviceName, address, interval);
                HeartBeatDetector.updateHeartBeat(serviceName, address, interval);
                return;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.info("【ROUND {}】【{}】心跳检测失败，重试中...", retryNum, address);
                retryNum++;
            }
        }
        log.info("【{}】心跳检测已下线", address);
        HeartBeatDetector.removeHost(serviceName, address);
    }
}
