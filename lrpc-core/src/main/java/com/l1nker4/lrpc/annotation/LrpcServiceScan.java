package com.l1nker4.lrpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置扫描的路径
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/6/10
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LrpcServiceScan {

    String[] value() default "";
}
