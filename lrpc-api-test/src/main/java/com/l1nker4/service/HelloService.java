package com.l1nker4.service;

import com.l1nker4.entity.HelloRequestEntity;

/**
 * @author ：L1nker4
 * @date ： 创建于  2024/03/02 16:52
 * @description： 测试API
 */
public interface HelloService {

    /**
     * 测试API
     * @param object
     * @return
     */
    String hello(HelloRequestEntity object);

}
