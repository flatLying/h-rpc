package top.dreamer.service.module.processor.serializer.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import top.dreamer.service.module.processor.serializer.HSerializer;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:10
 * @description: FastJson方法
 */
@Slf4j
public class FastJsonSerializer implements HSerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data, clazz, JSONReader.Feature.SupportClassForName);
    }
}
