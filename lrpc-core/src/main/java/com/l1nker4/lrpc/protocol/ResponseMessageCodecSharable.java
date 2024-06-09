package com.l1nker4.lrpc.protocol;


import com.l1nker4.lrpc.config.Config;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.BaseMessage;
import com.l1nker4.lrpc.enumeration.SerializerType;
import com.l1nker4.lrpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的
 *
 * @author l1nker4
 */
@Slf4j
@ChannelHandler.Sharable
public class ResponseMessageCodecSharable extends MessageToMessageCodec<ByteBuf, BaseMessage> {

    @Override
    public void encode(ChannelHandlerContext ctx, BaseMessage msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeBytes(Constants.MAGIC_NUMBER);
        out.writeByte(Constants.VERSION);
        SerializerType serializerType = Config.getSerializerType();
        out.writeByte(serializerType.ordinal());

        //package type
        out.writeInt(0);
        byte[] data = CommonSerializer.getByType(serializerType).serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(Constants.RETAIN_DATA);
        out.writeBytes(data);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNumber = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        int packageType = in.readInt();
        int length = in.readInt();
        in.readByte();
        in.readByte();
        byte[] data = new byte[length];

        in.readBytes(data, 0, length);

        CommonSerializer serializer = CommonSerializer.getByType(SerializerType.getByCode(serializerType));
        Class<? extends BaseMessage> messageClass = BaseMessage.getMessageClass(packageType);
        BaseMessage baseMessage = (BaseMessage) serializer.deserialize(messageClass, data);
        out.add(baseMessage);
    }

}
