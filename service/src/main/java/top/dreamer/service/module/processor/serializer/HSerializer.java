package top.dreamer.service.module.processor.serializer;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:08
 * @description: 序列化器
 */
public interface HSerializer {
    /**
     * 序列化
     * @param obj 输入
     * @return 字节数组
     * @param <T>
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param data 字节数组
     * @param clazz 类
     * @return 类实例
     * @param <T>
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
