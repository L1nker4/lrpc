package com.l1nker4.lrpc.server;

import com.sun.org.apache.xpath.internal.operations.String;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 16:56
 */
public class ServerTest {

    @Test
    public void testStart() {
        RpcServer rpcServer = new RpcServer();
        rpcServer.start(8080);
    }

    @Test
    public void testJSON() {
        byte[] data = {123, 34, 105, 110, 116, 101, 114, 102, 97, 99, 101, 78, 97, 109, 101, 34, 58, 34, 116, 101, 115, 116, 34, 44, 34, 109, 101, 116, 104, 111, 100, 78, 97, 109, 101, 34, 58, 34, 116, 101, 115, 116, 34, 44, 34, 112, 97, 114, 97, 109, 84, 121, 112, 101, 115, 34, 58, 91, 34, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 73, 110, 116, 101, 103, 101, 114, 34, 93, 44, 34, 112, 97, 114, 97, 109, 101, 116, 101, 114, 115, 34, 58, 91, 49, 44, 50, 93, 44, 34, 114, 101, 113, 117, 101, 115, 116, 73, 100, 34, 58, 49, 125};
        System.out.println(data.length);
    }
}
