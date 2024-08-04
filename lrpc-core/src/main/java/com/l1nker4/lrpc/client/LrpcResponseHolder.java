package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.entity.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author l1nker4
 * @Description
 * @Date 2024-07-10 10:14
 */
public class LrpcResponseHolder {

    //no public constructor
    private LrpcResponseHolder(){

    }

    //Map requestID—— response
    private static final Map<String, RpcResponse<?>> responseMap = new ConcurrentHashMap<>();

    public static void put(String requestId, RpcResponse<?> response){
        responseMap.put(requestId, response);
    }

    public static RpcResponse<?> get(String requestId){
        return responseMap.remove(requestId);
    }
}
