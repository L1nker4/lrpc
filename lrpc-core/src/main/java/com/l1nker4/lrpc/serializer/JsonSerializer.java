package com.l1nker4.lrpc.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.exception.SerializeException;
import com.sun.org.apache.xpath.internal.operations.String;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 16:39
 */
@Slf4j
public class JsonSerializer implements CommonSerializer {
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            return JSONObject.parseObject(bytes, clazz);
        } catch (Exception e) {
            log.error("JSON反序列化出错，data: {}", bytes, e);
            throw new SerializeException("反序列化出错");
        }
    }

    @Override
    public <T> byte[] serialize(T object) {
        try {
            return JSONObject.toJSONBytes(object);
        } catch (Exception e) {
            log.error("JSON序列化出错, data: {}", object, e);
            throw new SerializeException("序列化出错");
        }
    }


}
