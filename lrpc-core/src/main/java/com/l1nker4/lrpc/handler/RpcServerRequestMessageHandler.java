package com.l1nker4.lrpc.handler;


import com.l1nker4.lrpc.entity.RpcRequest;
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
 * 处理RPC请求的Handler
 * @author l1nker4
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcServerRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequest> {

    public static final Map<String, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        log.info("rpcRequest: {}", rpcRequest);
        RpcResponse<String> response = getResponseData(rpcRequest);
        channelHandlerContext.writeAndFlush(response);
    }

    private RpcResponse<String> getResponseData(RpcRequest rpcRequest) {
        RpcResponse<String> response = new RpcResponse<>();
        response.setRequestId(rpcRequest.getRequestId());
        response.setCode(ResponseCode.SUCCESS).setData("yes");
        return response;
    }

}
