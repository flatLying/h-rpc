package top.dreamer.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.ObjectUtils;
import top.dreamer.cache.core.HCache;
import top.dreamer.core.exception.HRpcFrameworkException;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 12:59
 * @description: caffeine支持的缓存
 */
public class HCaffeine implements HCache {

    /**
     * 本地的缓存
     */
    private final Cache<String, Object> cache;

    public HCaffeine() {
        this.cache = Caffeine.newBuilder().build();
    }

    @Override
    public void put(String key, Object value) {
        if (ObjectUtils.isNotEmpty(value)) {
            this.cache.put(key, value);
        } else {
            throw new HRpcFrameworkException("设置缓存value为 null");
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = this.cache.getIfPresent(key);
        if (ObjectUtils.isNotEmpty(value)) {
            return clazz.cast(value);
        } else {
            return null;
        }
    }

    @Override
    public Object get(String key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public void remove(String key) {
        this.cache.invalidate(key);
    }

    @Override
    public boolean containsKey(String key) {
        return this.cache.getIfPresent(key) != null;
    }
}
