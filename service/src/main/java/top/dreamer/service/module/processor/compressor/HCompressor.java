package top.dreamer.service.module.processor.compressor;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:11
 * @description: 压缩器
 */
public interface HCompressor {
    /**
     * 压缩数据
     * @param data 数据
     * @return 压缩结果
     */
    byte[] compress(byte[] data);

    /**
     * 解压缩数据
     * @param data 数据
     * @return 解压缩结果
     */
    byte[] decompress(byte[] data);
}
