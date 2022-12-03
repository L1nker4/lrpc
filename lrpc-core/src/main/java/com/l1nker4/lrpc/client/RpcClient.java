package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.handler.RpcResponseMessageHandler;
import com.l1nker4.lrpc.protocol.MessageCodecSharable;
import com.l1nker4.lrpc.protocol.ProtocolFrameDecoder;
import com.l1nker4.lrpc.protocol.SequenceIdGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC请求客户端
 *
 * @author l1nker4
 */
@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
//                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new MessageCodecSharable());
                    ch.pipeline().addLast(new RpcResponseMessageHandler());
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();

            RpcRequest rpcRequest = new RpcRequest("test", "test",
                    new Object[]{1, 2}, null);
            rpcRequest.setRequestId(SequenceIdGenerator.nextId());
            ChannelFuture future = channel.writeAndFlush(rpcRequest).addListener(promise -> {
                if (!promise.isSuccess()) {
                    Throwable cause = promise.cause();
                    log.error("error", cause);
                }
            });
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
