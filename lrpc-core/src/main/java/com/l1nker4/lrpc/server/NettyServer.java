package com.l1nker4.lrpc.server;

import com.l1nker4.lrpc.handler.RpcServerRequestMessageHandler;
import com.l1nker4.lrpc.protocol.ProtocolFrameDecoder;
import com.l1nker4.lrpc.protocol.RequestMessageCodecSharable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * RPC Server
 *
 * @author l1nker4
 */
@Slf4j
public class NettyServer extends AbstractServer {


    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    public NettyServer(String host, int port) {
        super(host, port);

    }

    @Override
    public void doInit() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    @Override
    public void doStart(String host, int port) {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(bossGroup, workerGroup);

            //// keep alive and deny nagle
            serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {

                    ch.pipeline().addLast(new ProtocolFrameDecoder());
//                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new RequestMessageCodecSharable());
                    ch.pipeline().addLast(new RpcServerRequestMessageHandler());
                }
            });
            serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        }
    }

    @Override
    public void doDestroy() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
