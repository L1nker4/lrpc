package com.l1nker4.test;

import com.l1nker4.entity.HelloRequestEntity;
import com.l1nker4.lrpc.proxy.RpcClientProxy;
import com.l1nker4.service.HelloService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author ：L1nker4
 * @date ： 创建于  2024/3/2 16:13
 */
@Slf4j
public class RpcClientTest {

    public static void main(String[] args) throws IOException {
        RpcClientProxy proxy = new RpcClientProxy();
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloRequestEntity param = new HelloRequestEntity();
        param.setId(1);
        param.setMessage("world");
        String res = helloService.hello(param);
        log.info("helloService result: {}", res);
    }
}
