package top.dreamer.service.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import top.dreamer.core.exception.HRpcBusinessException;
import top.dreamer.service.module.message.request.HRequest;

import static top.dreamer.service.common.constants.MessageConstants.MAGIC;

/**
 * @author HeYang
 * @Github <a href="https://github.com/flatLying">HeYang Github</a>
 * @date 2024-10-11 18:27
 * @description: 把request转为ByteBuf
 */
public class HRequestParser {
    /**
     * 把HRequest转为ByteBuf
     * @param request request
     * @return byte
     */
    public static ByteBuf encode(HRequest request) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(request.getFullLength());
        buf.writeBytes(request.getMagic());
        buf.writeByte(request.getVersion());
        buf.writeShort(request.getHeaderLength());
        buf.writeInt(request.getFullLength());
        buf.writeByte(request.getCompressType());
        buf.writeByte(request.getSerializeType());
        buf.writeByte(request.getMessageType());
        buf.writeLong(request.getMessageId());
        buf.writeBytes(request.getHRequestPayLoad());
        return buf;
    }

    public static HRequest decode(ByteBuf buf) {
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
        int payloadLength = fullLength - headerLength;
        byte[] hRequestPayload = new byte[payloadLength];
        buf.readBytes(hRequestPayload);

        return HRequest.builder()
                .magic(magic)
                .version(version)
                .headerLength(headerLength)
                .fullLength(fullLength)
                .compressType(compressType)
                .serializeType(serializeType)
                .messageType(messageType)
                .messageId(messageId)
                .hRequestPayLoad(hRequestPayload)
                .build();
    }
}
