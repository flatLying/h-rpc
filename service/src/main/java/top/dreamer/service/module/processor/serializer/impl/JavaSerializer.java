package top.dreamer.service.module.processor.serializer.impl;

import lombok.extern.slf4j.Slf4j;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.processor.serializer.HSerializer;

import java.io.*;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:10
 * @description: java内置的Stream方法
 */
@Slf4j
public class JavaSerializer implements HSerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new HRpcBusinessException("JAVA 序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            return clazz.cast(ois.read());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new HRpcBusinessException("JAVA 解序列化失败");
        }
    }
}
