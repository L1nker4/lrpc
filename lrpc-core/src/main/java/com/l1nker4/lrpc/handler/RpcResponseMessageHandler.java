package com.l1nker4.lrpc.handler;

import com.l1nker4.lrpc.entity.RpcResponse;
import com.l1nker4.lrpc.enumeration.ResponseCode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC响应Handler
 * @author ：L1nker4
 * @date ： 创建于  2022/12/1 22:34
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponse> {

    public static final Map<Long, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        log.debug("response: {}", rpcResponse);
        // 拿到空的 promise
        Promise<Object> promise = PROMISES.remove(rpcResponse.getRequestId());
        if (promise != null) {
            Object returnData = rpcResponse.getData();
            if (ResponseCode.SUCCESS.equals(rpcResponse.getCode())) {
                promise.setSuccess(returnData);
            } else {
                promise.setFailure(new Exception(String.format("RPC请求出错, data；%s", returnData)));
            }
        }
    }
}
