package top.dreamer.service.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.common.context.HRequestContext;
import top.dreamer.service.module.enums.HCompressType;
import top.dreamer.service.module.enums.HSerializeType;
import top.dreamer.service.module.message.common.HHeader;
import top.dreamer.service.module.message.request.HRequest;
import top.dreamer.service.module.message.response.HResponse;
import top.dreamer.service.module.message.response.HResponsePayLoad;
import top.dreamer.service.module.processor.serializer.HSerializer;

import static top.dreamer.service.common.constants.MessageConstants.MAGIC;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:27
 * @description: 把request转为ByteBuf
 */
public class HMessageParser {
    /**
     * 把HRequest转为ByteBuf
     * 1. 读Header
     * 2. 读PayLoad
     * @param request request
     * @return byte
     */
    public static ByteBuf encodeHRequest(HRequest request) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(request.getHeader().getFullLength());
        ByteBuf headBuf = HMessageParser.encodeHHeader(request.getHeader());

        byte[] payLoadBytes = HSerializeType.getByCode(request.getHeader().getSerializeType()).getSerializer().serialize(request.getHRequestPayLoad());
        payLoadBytes = HCompressType.getByCode(request.getHeader().getCompressType()).getCompressor().compress(payLoadBytes);

        buf.writeBytes(headBuf);
        buf.writeBytes(payLoadBytes);
        return buf;
    }


    /**
     * 序列化HHeader
     * @param header HHeader
     * @return ByteBuf
     */
    public static ByteBuf encodeHHeader(HHeader header) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(header.getHeaderLength());
        buf.writeBytes(header.getMagic());
        buf.writeByte(header.getVersion());
        buf.writeShort(header.getHeaderLength());
        buf.writeInt(header.getFullLength());
        buf.writeByte(header.getCompressType());
        buf.writeByte(header.getSerializeType());
        buf.writeByte(header.getMessageType());
        buf.writeLong(header.getMessageId());
        buf.writeLong(header.getTimeStamp());
        return buf;
    }


    /**
     * 还原请求头
     * @param buf byteBuf
     * @return HHeader
     */
    private static HHeader decodeHHeader(ByteBuf buf) {
        byte[] magic = new byte[MAGIC.length];
        buf.readBytes(magic);
        // 校验魔术值是否正确
        for (int i = 0; i < MAGIC.length; i++) {
            if (magic[i] != MAGIC[i]) {
                throw new HRpcBusinessException("消息MAGIC字段不正确");
            }
        }
        byte version = buf.readByte();
        short headerLength = buf.readShort();
        int fullLength = buf.readInt();
        byte compressType = buf.readByte();
        byte serializeType = buf.readByte();
        byte messageType = buf.readByte();
        long messageId = buf.readLong();
        long tieStamp = buf.readLong();

        return HHeader.builder().magic(magic)
                .version(version)
                .headerLength(headerLength)
                .fullLength(fullLength)
                .compressType(compressType)
                .serializeType(serializeType)
                .messageType(messageType)
                .messageId(messageId)
                .timeStamp(tieStamp).build();
    }

    /**
     * 还原部分context
     * 注意！！！
     *  这个部分只能得到 Header和 payLoadBytes
     *  无法得到完整的HRequest
     * @param in byteBuf
     * @return context
     */
    public static HRequestContext decodePayLoadBytesAndHeader(ByteBuf in) {
        HHeader hHeader = HMessageParser.decodeHHeader(in);
        // 读取剩下的字节并填充到 byte[] 中
        int readableBytes = in.readableBytes();
        byte[] payLoadBytes = new byte[readableBytes];
        in.readBytes(payLoadBytes);

        HRequest hRequest = HRequest.builder().header(hHeader).build();

        return HRequestContext.builder().request(hRequest)
                .header(hHeader).payLoadBytes(payLoadBytes).build();
    }

    /**
     * 还原HResponse
     * @param in byteBuf
     * @return HResponse
     */
    public static HResponse decodeHResponse(ByteBuf in) {
        HHeader hHeader = HMessageParser.decodeHHeader(in);
        int code = in.readInt();

        int readableBytes = in.readableBytes();
        byte[] payloadBytes = new byte[readableBytes];
        in.readBytes(payloadBytes);  // 读取未读的字节
        HSerializer serializer = HSerializeType.getByCode(hHeader.getSerializeType()).getSerializer();
        HResponsePayLoad payLoad = serializer.deserialize(payloadBytes, HResponsePayLoad.class);
        return HResponse.builder().header(hHeader)
                .code(code).payLoad(payLoad).build();
    }
}
