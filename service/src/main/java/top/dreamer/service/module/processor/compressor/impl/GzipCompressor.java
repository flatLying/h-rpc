package top.dreamer.service.module.processor.compressor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.processor.compressor.HCompressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:14
 * @description: Gzip压缩
 */
public class GzipCompressor implements HCompressor {
    private static final Logger log = LoggerFactory.getLogger(GzipCompressor.class);

    @Override
    public byte[] compress(byte[] data) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            gos.write(data);
            gos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new HRpcBusinessException("GZIP 压缩失败");
        }
    }

    @Override
    public byte[] decompress(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            GZIPInputStream gis = new GZIPInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new HRpcBusinessException("GZIP 解压缩失败");
        }
    }
}
