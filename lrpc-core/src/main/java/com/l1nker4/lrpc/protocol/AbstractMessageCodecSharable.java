package com.l1nker4.lrpc.protocol;


import com.l1nker4.lrpc.config.Config;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.BaseMessage;
import com.l1nker4.lrpc.enumeration.SerializerType;
import com.l1nker4.lrpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的
 *
 * @author l1nker4
 */
@Slf4j
public abstract class AbstractMessageCodecSharable {

    public ByteBuf buildEncodeMessage(ChannelHandlerContext ctx, BaseMessage msg, Integer packageType) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeBytes(Constants.MAGIC_NUMBER);
        out.writeByte(Constants.VERSION);
        SerializerType serializerType = Config.getSerializerType();
        out.writeByte(serializerType.ordinal());

        //package type
        out.writeInt(packageType);
        byte[] data = CommonSerializer.getByType(serializerType).serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(Constants.RETAIN_DATA);
        out.writeBytes(data);
        return out;
    }

    public BaseMessage buildDecodeMessage(ByteBuf byteBuf) throws Exception {
        int magicNumber = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        int packageType = byteBuf.readInt();
        int length = byteBuf.readInt();
        byteBuf.readByte();
        byteBuf.readByte();
        byte[] data = new byte[length];

        byteBuf.readBytes(data, 0, length);

        CommonSerializer serializer = CommonSerializer.getByType(SerializerType.getByCode(serializerType));
        Class<? extends BaseMessage> messageClass = BaseMessage.getMessageClass(packageType);
        BaseMessage baseMessage = (BaseMessage) serializer.deserialize(messageClass, data);
        return baseMessage;
    }

}
