package com.l1nker4.lrpc.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 序号生成器
 *
 * @author l1nker4
 */
public abstract class SequenceIdGenerator {

    private static final AtomicInteger ID = new AtomicInteger();

    public static int nextId() {
        return ID.incrementAndGet();
    }
}