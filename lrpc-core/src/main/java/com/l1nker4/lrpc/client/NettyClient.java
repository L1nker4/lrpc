package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.entity.RpcResponse;
import com.l1nker4.lrpc.handler.RpcClientResponseMessageHandler;
import com.l1nker4.lrpc.protocol.RequestMessageCodecSharable;
import com.l1nker4.lrpc.protocol.ProtocolFrameDecoder;
import com.l1nker4.lrpc.protocol.ResponseMessageCodecSharable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * Netty客户端实现
 *
 * @author ：L1nker4
 * @date ： 创建于  2024/3/3 15:41
 */
@Slf4j
public class NettyClient implements RpcClient {
    @Override
    public Object sendRequest(RpcRequest request) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ResponseMessageCodecSharable());
                    ch.pipeline().addLast(new RpcClientResponseMessageHandler());
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            log.info("rpc client request : {}", request);
            ChannelFuture future = channel.writeAndFlush(request).addListener((ChannelFutureListener) promise -> {
                if (!promise.isSuccess()) {
                    promise.channel().close();
                    resultFuture.completeExceptionally(promise.cause());
                    log.error("发送消息时有错误发生: ", promise.cause());
                }
            });
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
        return resultFuture;
    }
}
