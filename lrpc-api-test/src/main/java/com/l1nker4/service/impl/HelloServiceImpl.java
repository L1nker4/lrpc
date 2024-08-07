package com.l1nker4.service.impl;

import com.l1nker4.entity.HelloRequestEntity;
import com.l1nker4.lrpc.annotation.LrpcService;
import com.l1nker4.service.HelloService;

@LrpcService
public class HelloServiceImpl implements HelloService
{
    @Override
    public String hello(HelloRequestEntity object) {
        return "hello" + object.getMessage();
    }
}
