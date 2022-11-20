package com.l1nker4.lrpc.server;

import org.junit.jupiter.api.Test;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 16:56
 */
public class ServerTest {

    @Test
    public void testStart(){
        RpcServer rpcServer = new RpcServer();
        rpcServer.start(8080);
    }
}
