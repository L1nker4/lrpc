package com.l1nker4.test;

import com.l1nker4.lrpc.client.NettyClient;
import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.entity.RpcResponse;
import com.l1nker4.lrpc.protocol.SequenceIdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @author ：L1nker4
 * @date ： 创建于  2024/3/2 16:13
 */
@Slf4j
public class RpcClientTest {

    public static void main(String[] args) {
        RpcRequest rpcRequest = new RpcRequest("test", "test",
                new Object[]{1, 2}, null);
        rpcRequest.setRequestId(SequenceIdGenerator.nextId());

        NettyClient nettyClient = new NettyClient();

        CompletableFuture<RpcResponse> result = (CompletableFuture<RpcResponse>) nettyClient.sendRequest(rpcRequest);
        try {
            RpcResponse rpcResponse = result.get();
            log.info("client receive response: {}", rpcResponse);
        } catch (Exception e) {
            log.error("error ", e);
        }
    }
}
