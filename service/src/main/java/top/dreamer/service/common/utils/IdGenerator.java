package top.dreamer.service.common.utils;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-12 14:38
 * @description: ID生成
 */
public class IdGenerator {
    // 开始时间戳（2024-01-01 00:00:00）
    private static final long START_TIMESTAMP = 1704067200000L;

    // 每部分的位数
    private static final long SEQUENCE_BITS = 12; // 序列号占用12位
    private static final long MACHINE_BITS = 5; // 机器ID占用5位
    private static final long DATA_CENTER_BITS = 5; // 数据中心ID占用5位

    // 每部分最大值
    private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BITS);
    private static final long MAX_MACHINE_ID = -1L ^ (-1L << MACHINE_BITS);
    private static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_BITS);

    // 左移位数
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_SHIFT = SEQUENCE_BITS + MACHINE_BITS;
    private static final long TIMESTAMP_SHIFT = DATA_CENTER_SHIFT + DATA_CENTER_BITS;

    // 成员变量
    private long dataCenterId; // 数据中心ID
    private long machineId; // 机器ID
    private long sequence = 0L; // 序列号
    private long lastTimestamp = -1L; // 上次生成ID的时间戳

    public IdGenerator() {
        this(1L, 1L);
    }

    // 构造函数
    public IdGenerator(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("数据中心ID不能超过 %d 或小于 0", MAX_DATA_CENTER_ID));
        }
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException(String.format("机器ID不能超过 %d 或小于 0", MAX_MACHINE_ID));
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    // 生成下一个ID，线程安全
    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(String.format("时钟回拨，拒绝生成ID，直到 %d", lastTimestamp));
        }

        if (currentTimestamp == lastTimestamp) {
            // 同一毫秒内，序列号递增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号达到最大值，则等待下一毫秒
            if (sequence == 0L) {
                currentTimestamp = waitForNextMillis(currentTimestamp);
            }
        } else {
            // 不同毫秒内，序列号从0开始
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 组合ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_SHIFT)
                | (machineId << MACHINE_SHIFT)
                | sequence;
    }

    // 等待下一个毫秒
    private long waitForNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = getCurrentTimestamp();
        }
        return currentTimestamp;
    }

    // 获取当前时间戳（毫秒）
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        IdGenerator idGenerator = new IdGenerator(1, 1); // 数据中心ID=1，机器ID=1
        for (int i = 0; i < 10; i++) {
            System.out.println(idGenerator.nextId());
        }
    }
}

