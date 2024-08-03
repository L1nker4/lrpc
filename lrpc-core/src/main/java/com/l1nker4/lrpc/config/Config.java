package com.l1nker4.lrpc.config;

import com.l1nker4.lrpc.enumeration.SerializerType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 17:05
 */
@Slf4j
public abstract class Config {

    static Properties properties;

    private static final Map<String, String> configMap = new ConcurrentHashMap<>();

    static {
        try (InputStream in = Config.class.getResourceAsStream("/config.properties")) {
            properties = new Properties();
            properties.load(in);
            initMap();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void initMap() {
        properties.forEach((key, value) -> {
            configMap.put((String) key, (String) value);
        });
    }

    public static Object getByName(String name) {
        return configMap.get(name);
    }

    public static String set(String key, String value) {
        log.info("update config: {}, configValue: {}", key, value);
        return configMap.putIfAbsent(key, value);
    }

    public static SerializerType getSerializerType() {
        String value = configMap.get("serializer.algorithm");
        if (value == null) {
            //默认配置为JSON方式
            return SerializerType.JSON;
        } else {
            return SerializerType.valueOf(value);
        }
    }
}
