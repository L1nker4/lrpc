package com.l1nker4.lrpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ：L1nker4
 * @description: 服务注解
 * @date ： 创建于  2024/6/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LrpcService {

    String value() default "";
}
