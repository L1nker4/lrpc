package com.l1nker4.lrpc.utils;

import de.huxhorn.sulky.ulid.ULID;

/**
 * @Author wanglin
 * @Description
 * @Date 2024-07-10 10:24
 */
public class RequestIdGenerator {

    private static final ULID ULID_GENERATOR = new ULID();

    private RequestIdGenerator(){

    }

    public static String nextId() {
        return ULID_GENERATOR.nextULID();
    }
}
