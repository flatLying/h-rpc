package top.dreamer.schedule;

import lombok.extern.slf4j.Slf4j;
import top.dreamer.core.exception.HRpcFrameworkException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 23:01
 * @description: 抽象实现定时任务，使用共享线程池
 */
@Slf4j
public abstract class HScheduleTaskAbstract {

    // 全局共享的调度线程池，线程池大小可以根据实际需要调整
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    // 存储任务的 key 和对应的 List<Future>，允许同一个 key 对应多个任务
    private static final ConcurrentHashMap<String, List<ScheduledFuture<?>>> tasks = new ConcurrentHashMap<>();

    public abstract void executeScheduledTask();


    public void scheduleTask(String key, long delay, long period) {
        // 启动任务并存储 Future
        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(this::executeScheduledTask, delay, period, TimeUnit.SECONDS);

        // 如果 key 已存在，则添加到现有的任务列表中；否则新建一个列表
        tasks.computeIfAbsent(key, k -> new ArrayList<>()).add(future);

        log.info("任务【{}】已启动", key);
    }

    public static void stopTask(String key) {
        // 根据 key 获取任务列表，并取消所有关联的任务
        List<ScheduledFuture<?>> futures = tasks.get(key);
        if (futures != null && !futures.isEmpty()) {
            futures.forEach(future -> {
                if (!future.isCancelled()) {
                    future.cancel(true);  // 取消任务
                }
            });
            tasks.remove(key);  // 从任务列表中移除 key
            log.info("任务【{}】的所有实例已停止", key);
        } else {
            log.info("任务【{}】不存在或已取消", key);
        }
    }

    /**
     * 停止所有的调度器
     */
    public static void stopAllTasks() {
        scheduler.shutdown();
        tasks.clear();
        log.info("所有任务已停止");
    }
}
