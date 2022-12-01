package com.l1nker4.lrpc.protocol;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 序号生成器
 *
 * @author l1nker4
 */
public abstract class SequenceIdGenerator {

    private static final AtomicLong ID = new AtomicLong();

    public static Long nextId() {
        return ID.incrementAndGet();
    }
}