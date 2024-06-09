package com.l1nker4.lrpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author ：L1nker4
 * @description: Hessioon序列化实现
 * @date ： 创建于  2024/6/9
 */
@Slf4j
public class HessianSerializer implements CommonSerializer {


    @Override
    public <T> Object deserialize(Class<T> clazz, byte[] bytes) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(bais);
            return hessianInput.readObject();
        } catch (IOException e) {
            log.error("serialize hessian2 input error", e);
        } finally {
            if (hessianInput != null) {
                hessianInput.close();
            }
        }
        return null;
    }

    @Override
    public <T> byte[] serialize(T object) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("serialize hessian2Output error", e);
            throw new RuntimeException("serialize hessian2Output error", e);
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (Exception e) {
                    log.error("serialize hessian2Output close error", e);
                }
            }
        }
    }
}
