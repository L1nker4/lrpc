package com.l1nker4.lrpc.serializer;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.exception.SerializeException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 16:39
 */
@Slf4j
public class JsonSerializer implements CommonSerializer {
    @Override
    public <T> Object deserialize(Class<T> clazz, byte[] bytes) {
        try {
            Object object = JSONObject.parseObject(new String(bytes, StandardCharsets.UTF_8), clazz, JSONReader.Feature.SupportClassForName);
            if (object instanceof RpcRequest) {
                object = handleParamType(object);
            }
            return object;
        } catch (Exception e) {
            log.error("JSON反序列化出错，data: {}", bytes, e);
            throw new SerializeException("反序列化出错");
        }
    }

    /**
     * 重写param类型
     * @param object
     * @return
     */
    private RpcRequest handleParamType(Object object) {
        RpcRequest request = (RpcRequest) object;
        for (int i = 0; i < request.getParamTypes().length; i++) {
            Class<?> paramTypeClazz = request.getParamTypes()[i];
            Object currParameter = request.getParameters()[i];
            if (!paramTypeClazz.isAssignableFrom(currParameter.getClass())) {
                request.getParameters()[i] = JSON.to(paramTypeClazz, currParameter);
            }
        }
        return request;
    }

    @Override
    public <T> byte[] serialize(T object) {
        try {
            return JSONObject.toJSONString(object).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("JSON序列化出错, data: {}", object, e);
            throw new SerializeException("序列化出错");
        }
    }


}
