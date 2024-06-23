package com.l1nker4.test;

import com.l1nker4.lrpc.annotation.LrpcServiceScan;
import com.l1nker4.lrpc.provider.ServiceProvider;
import com.l1nker4.lrpc.provider.ServiceProviderFactory;
import com.l1nker4.lrpc.server.RpcServer;
import com.l1nker4.service.HelloService;
import com.l1nker4.service.impl.HelloServiceImpl;

/**
 * @author ：L1nker4
 * @date ： 创建于  2024/3/2 16:13
 */
@LrpcServiceScan(value = {"com.l1nker4.service.impl"})
public class RpcServerTest {

    public static void main(String[] args) {
        ServiceProvider provider = ServiceProviderFactory.getProvider();
        HelloService service = new HelloServiceImpl();
        provider.addServiceProvider("com.l1nker4.service.HelloService", service);

        RpcServer rpcServer = new RpcServer("127.0.0.1", 8080);
        rpcServer.start();
    }

}
