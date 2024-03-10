package com.l1nker4.lrpc.serializer;

/**
 * Kyro算法序列化实现
 * @author ：L1nker4
 * @date ： 创建于  2024/3/9 20:48
 */
public class KyroSerializer implements CommonSerializer{
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return null;
    }

    @Override
    public <T> byte[] serialize(T object) {
        return new byte[0];
    }
}
