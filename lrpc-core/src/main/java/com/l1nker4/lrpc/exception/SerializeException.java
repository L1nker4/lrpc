package com.l1nker4.lrpc.exception;

/**
 * 序列化异常
 *
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 16:45
 */
public class SerializeException extends RuntimeException {

    public SerializeException(String msg) {
        super(msg);
    }
}
