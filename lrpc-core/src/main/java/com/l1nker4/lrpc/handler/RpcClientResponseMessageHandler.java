package com.l1nker4.lrpc.handler;

import com.l1nker4.lrpc.client.LrpcResponseHolder;
import com.l1nker4.lrpc.entity.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC响应Handler
 * @author ：L1nker4
 * @date ： 创建于  2022/12/1 22:34
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcClientResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponse> {

//    private final CompletableFuture<RpcResponse> responseFuture;

    public RpcClientResponseMessageHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        LrpcResponseHolder.put(rpcResponse.getRequestId(), rpcResponse);
        log.info("rpc response: {}", rpcResponse);
    }
}
