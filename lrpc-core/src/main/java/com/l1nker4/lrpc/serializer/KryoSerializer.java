package com.l1nker4.lrpc.serializer;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.esotericsoftware.kryo.kryo5.util.Pool;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kyro算法序列化实现
 *
 * @author ：L1nker4
 * @date ： 创建于  2024/3/9 20:48
 */
@Slf4j
public class KryoSerializer implements CommonSerializer {

    private final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 20) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Kryo kryo = kryoPool.obtain();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            Input input = new Input(bais);
            return (T) kryo.readClassAndObject(input);
        } catch (IOException e) {
            log.error("Kryo deserialization error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> byte[] serialize(T object) {
        Kryo kryo = kryoPool.obtain();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Output output = new Output(bos)) {
            kryo.writeClassAndObject(output, object);
            return output.toBytes();
        } catch (IOException e) {
            log.error("Kryo serialization error", e);
            throw new RuntimeException(e);
        }
    }

}
