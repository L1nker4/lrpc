package com.l1nker4.lrpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 序列化方法-枚举类
 *
 * @author ：L1nker4
 * @date ： 创建于  2022/11/19 23:22
 */
@Getter
@AllArgsConstructor
public enum SerializerType {

    /**
     *
     */
    JSON(1),
    PROTOBUF(2);

    private final int code;

    public static SerializerType getByCode(int code){
        for (SerializerType value : values()) {
            if (value.getCode() == code){
                return value;
            }
        }
        return JSON;
    }

}
