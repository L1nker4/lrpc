package com.l1nker4.test;

import com.l1nker4.lrpc.annotation.LrpcServiceScan;
import com.l1nker4.lrpc.server.RpcServer;

/**
 * @author ：L1nker4
 * @date ： 创建于  2024/3/2 16:13
 */
@LrpcServiceScan(value = {"com.l1nker4.service.impl"})
public class RpcServerTest {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer("127.0.0.1", 8082);
        rpcServer.start();
    }

}
