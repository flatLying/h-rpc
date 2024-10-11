package top.dreamer.cache.core;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 12:50
 * @description: Cache模块
 */
public interface HCache {

    /**
     * 设置缓存
     *  没有则放置
     *  存在则更新
     * @param key 缓存Key
     * @param value 缓存值
     */
    void put(String key, Object value);

    /**
     * 获取缓存的数据
     * @param key key
     * @param clazz 转换的类别
     * @return value
     * @param <T> 泛型
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 通过key获取缓存
     * @param key key
     * @return Object结果
     */
    Object get(String key);

    /**
     * 删除缓存
     * @param key key
     */
    void remove(String key);

    /**
     * 判断是否存在key
     * @param key key
     * @return Boolean
     */
    boolean containsKey(String key);
}
