package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.handler.RpcClientResponseMessageHandler;
import com.l1nker4.lrpc.protocol.ProtocolFrameDecoder;
import com.l1nker4.lrpc.protocol.ResponseMessageCodecSharable;
import com.l1nker4.lrpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/8/4
 */
@Slf4j
public class NettyChannelPoolFactory {

    private static final Map<String, Map<Class<? extends CommonSerializer> , ArrayBlockingQueue<Channel>>> channelPoolMap = new ConcurrentHashMap<>();

    private static final Integer MAX_CHANNEL_SIZE = 10;

    private NettyChannelPoolFactory() {

    }

    public static final NettyChannelPoolFactory getInstance() {
        return NettyChannelPoolFactoryHolder.INSTANCE;
    }


    public Channel getChannel(String address, Class<? extends CommonSerializer> serializerType, long timeoutMills) {
        ArrayBlockingQueue<Channel> queue = getBlockingQueue(address, serializerType);
        Channel channel = null;
        try {
            channel = queue.poll(timeoutMills, TimeUnit.MILLISECONDS);
            if (null == channel
                    || !channel.isOpen()
                    || !channel.isActive()
                    || !channel.isWritable()) {
                log.debug("channel is not open or active or writable");
                channel = getNettyChannel(address, serializerType);
            }
        }catch (InterruptedException e){
            log.error("get item from blocking queue error", e);
        }
        return channel;
    }

    /**
     * get blocking queue
     * @param address
     * @param serializerType
     * @return
     */
    private ArrayBlockingQueue<Channel> getBlockingQueue(String address, Class<? extends CommonSerializer> serializerType) {
        if (null == channelPoolMap.get(address)) {
            Map<Class<? extends CommonSerializer>, ArrayBlockingQueue<Channel>> channelMap = new ConcurrentHashMap<>();
            channelPoolMap.put(address, channelMap);
        }
        if (null == channelPoolMap.get(address).get(serializerType)) {
            ArrayBlockingQueue<Channel> queue = new ArrayBlockingQueue<>(MAX_CHANNEL_SIZE);

            int currChannelSize = 0;
            while (currChannelSize < MAX_CHANNEL_SIZE) {
                Channel channel = getNettyChannel(address, serializerType);
                if (null != channel) {
                    queue.offer(channel);
                    currChannelSize++;
                }
            }
            channelPoolMap.get(address).put(serializerType, queue);
            return queue;
        } else {
            return channelPoolMap.get(address).get(serializerType);
        }
    }

    /**
     * init netty channel and connect to server
     * @param address
     * @param serializerType
     * @return
     */
    private Channel getNettyChannel(String address, Class<? extends CommonSerializer> serializerType) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class)
                    .group(group)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ProtocolFrameDecoder());
//                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ResponseMessageCodecSharable());
                            ch.pipeline().addLast(new RpcClientResponseMessageHandler());
                        }
                    }).option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);

            String[] splitStr = address.split(":");
            String host = splitStr[0];
            int port = Integer.parseInt(splitStr[1]);
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            final CountDownLatch connectedLatch = new CountDownLatch(1);

            final boolean[] successFlag = {false};
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    successFlag[0] = true;
                }else {
                    future.cause().printStackTrace();
                    successFlag[0] = false;
                }
                connectedLatch.countDown();
            });

            connectedLatch.await();
            if (successFlag[0]) {
                return channelFuture.channel();
            }
        } catch (Exception e) {
            log.error("create channel error", e);
        }
        return null;
    }

    public void releaseChannel(String address, Class<? extends CommonSerializer> serializerType, Channel channel) {
        if (!channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
            channel.deregister().syncUninterruptibly().awaitUninterruptibly();
            channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
        }
        getBlockingQueue(address, serializerType).offer(channel);
    }

    private static class NettyChannelPoolFactoryHolder {
        private static final NettyChannelPoolFactory INSTANCE = new NettyChannelPoolFactory();
    }
}
