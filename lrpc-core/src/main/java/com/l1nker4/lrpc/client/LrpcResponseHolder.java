package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.entity.RpcResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author l1nker4
 * @Description
 * @Date 2024-07-10 10:14
 */
public class LrpcResponseHolder {

    //no public constructor
    private LrpcResponseHolder() {

    }

    //Map requestID—— response
    private static final Map<String, HolderWrapper> responseMap = new ConcurrentHashMap<>();

    public static void put(String requestId, RpcResponse<?> response) {
        HolderWrapper holderWrapper = responseMap.get(requestId);
        holderWrapper.getBlockingQueue().add(response);
        responseMap.put(requestId, holderWrapper);
    }

    public static void initWrapper(String requestId){
        responseMap.put(requestId, HolderWrapper.builder().build());
    }

    public static RpcResponse<?> get(String requestId, long timeoutMillis) {
        HolderWrapper holderWrapper = responseMap.get(requestId);
        try {
            BlockingQueue<RpcResponse<?>> blockingQueue = holderWrapper.getBlockingQueue();
            return blockingQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
        }catch (InterruptedException e){
            throw new RuntimeException("get response error", e);
        }finally {
            responseMap.remove(requestId);
        }
    }

    @Getter
    @Builder
    static class HolderWrapper {

        private final BlockingQueue<RpcResponse<?>> blockingQueue = new ArrayBlockingQueue<>(1);

    }
}
