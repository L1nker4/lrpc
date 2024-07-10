package com.l1nker4.lrpc.proxy;

import com.l1nker4.lrpc.client.LrpcResponseHolder;
import com.l1nker4.lrpc.client.NettyClient;
import com.l1nker4.lrpc.client.RpcClient;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.entity.RpcResponse;
import com.l1nker4.lrpc.utils.RequestIdGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {

    private final RpcClient rpcClient;

    public RpcClientProxy() {
        rpcClient = new NettyClient();
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String interfaceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(RequestIdGenerator.nextId())
                .interfaceName(interfaceName)
                .version(Constants.DEFAULT_VERSION)
                .groupName(Constants.DEFAULT_GROUP_NAME)
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();

        rpcClient.sendRequest(rpcRequest);
        return LrpcResponseHolder.get(rpcRequest.getRequestId());
    }
}
