package com.l1nker4.lrpc.serializer;

import com.l1nker4.lrpc.enumeration.SerializerType;

/**
 * 序列化器接口
 * @author l1nker4
 */
public interface CommonSerializer {

    /**
     * 反序列化方法
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> Object deserialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化方法
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

    /**
     * 根据Type获取对应的序列化器
     * @param type
     * @return
     */
    static CommonSerializer getByType(SerializerType type){
        switch (type){
            case PROTOBUF:
                return new ProtobufSerializer();
            default:
                return new JsonSerializer();
        }
    }
}
