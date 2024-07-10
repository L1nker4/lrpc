package com.l1nker4.lrpc.handler;


import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.entity.RpcResponse;
import com.l1nker4.lrpc.enumeration.ResponseCode;
import com.l1nker4.lrpc.provider.ServiceProvider;
import com.l1nker4.lrpc.provider.ServiceProviderFactory;
import com.l1nker4.lrpc.registry.zookeeper.ZookeeperServiceRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理RPC请求的Handler
 * @author l1nker4
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcServerRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequest>  {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        log.info("rpcRequest: {}", rpcRequest);
        RpcResponse<?> response = invoke(rpcRequest);
        channelHandlerContext.writeAndFlush(response);
    }

    private RpcResponse<Object> invoke(RpcRequest rpcRequest) throws Exception {
        RpcResponse<Object> response = new RpcResponse<>();
        response.setRequestId(rpcRequest.getRequestId());

        Object result;
        //invoke the service
        String interfaceName = rpcRequest.getInterfaceName();
        ServiceProvider provider = ServiceProviderFactory.getProvider();
        try {
            Object service = provider.getServiceProvider(interfaceName);
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
        }catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
            return response.setData(e.getMessage())
                    .setCode(ResponseCode.FAILED);
        }
        response.setCode(ResponseCode.SUCCESS)
                .setData(result);
        return response;
    }


}
