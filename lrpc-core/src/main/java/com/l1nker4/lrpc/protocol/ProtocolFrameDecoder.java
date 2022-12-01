package com.l1nker4.lrpc.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 数据帧解码器
 *
 * @author l1nker4
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * maxFrameLength：最大帧长度。也就是可以接收的数据的最大长度。如果超过，此次数据会被丢弃。
     * lengthFieldOffset：长度域偏移。就是说数据开始的几个字节可能不是表示数据长度，需要后移几个字节才是长度域。
     * lengthFieldLength：长度域字节数。用几个字节来表示数据长度。
     * lengthAdjustment：数据长度修正。因为长度域指定的长度可以使header+body的整个长度，也可以只是body的长度。如果表示header+body的整个长度，那么我们需要修正数据长度。
     * initialBytesToStrip：跳过的字节数。如果你需要接收header+body的所有数据，此值就是0，如果你只想接收body数据，那么需要跳过header所占用的字节数。
     */
    public ProtocolFrameDecoder() {
        this(1024, 10,
                4, 3, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
