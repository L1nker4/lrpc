package com.l1nker4.lrpc.config;

import com.l1nker4.lrpc.enumeration.SerializerType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 17:05
 */
public abstract class Config {

    static Properties properties;

    static {
        try (InputStream in = Config.class.getResourceAsStream("/config.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Object getByName(String name) {
        return properties.getProperty(name);
    }

    public static SerializerType getSerializerType() {
        String value = properties.getProperty("serializer.algorithm");
        if (value == null) {
            return SerializerType.JSON;
        } else {
            return SerializerType.valueOf(value);
        }
    }
}
