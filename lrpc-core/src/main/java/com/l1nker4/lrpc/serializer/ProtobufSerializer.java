package com.l1nker4.lrpc.serializer;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 16:39
 */
public class ProtobufSerializer implements CommonSerializer{
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return null;
    }

    @Override
    public <T> byte[] serialize(T object) {
        return new byte[0];
    }
}
