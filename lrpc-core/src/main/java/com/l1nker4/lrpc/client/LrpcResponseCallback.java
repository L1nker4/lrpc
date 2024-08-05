package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.config.Config;
import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.entity.RpcResponse;
import com.l1nker4.lrpc.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @Author wanglin
 * @Description
 * @Date 2024-08-05 18:59
 */
@Builder
@Slf4j
public class LrpcResponseCallback implements Callable<RpcResponse<?>> {

    private String address;

    private RpcRequest request;

    protected long timeoutMillis;

    private final NettyChannelPoolFactory nettyChannelPoolFactory;

    private final Class<? extends CommonSerializer> serializerClass = CommonSerializer.getClassByType(Config.getSerializerType());


    @Override
    public RpcResponse<?> call() throws Exception {
        Channel channel = null;
        try {
            LrpcResponseHolder.initWrapper(request.getRequestId());
            channel = nettyChannelPoolFactory.getChannel(address, serializerClass, 5000);
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            channelFuture.syncUninterruptibly();
            return LrpcResponseHolder.get(request.getRequestId(), timeoutMillis);
        }catch (Exception e){
            log.error("send request error", e);
        }finally {
            if (null != channel) {
                nettyChannelPoolFactory.releaseChannel(address, serializerClass, channel);
            }
        }
        return null;
    }
}
