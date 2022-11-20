package com.l1nker4.lrpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 15:40
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseMessage {

    /**
     * 请求id
     */
    private String requestId;


    private static final AtomicInteger count = new AtomicInteger(0);

    private static final Map<Integer, Class<? extends BaseMessage>> MAP = new HashMap<>();

    static {
        MAP.put(count.getAndIncrement(), RpcRequest.class);
        MAP.put(count.getAndIncrement(), RpcResponse.class);

    }

    public static Class<? extends BaseMessage> getMessageClass(int messageType){
        return MAP.get(messageType);
    }


}
