package com.l1nker4.lrpc.server;

import com.l1nker4.lrpc.annotation.LrpcService;
import com.l1nker4.lrpc.annotation.LrpcServiceScan;
import com.l1nker4.lrpc.handler.RpcServerRequestMessageHandler;
import com.l1nker4.lrpc.protocol.ProtocolFrameDecoder;
import com.l1nker4.lrpc.protocol.RequestMessageCodecSharable;
import com.l1nker4.lrpc.provider.ServiceProvider;
import com.l1nker4.lrpc.provider.ServiceProviderFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.Set;

/**
 * RPC Server
 *
 * @author l1nker4
 */
@Slf4j
public class RpcServer {

    private final int port;

    private final ServiceProvider serviceProvider;

    public RpcServer(int port) {
        this.port = port;
        this.serviceProvider = ServiceProviderFactory.getProvider();
        scanService();
    }

    private void scanService() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[stackTrace.length - 1].getClassName();
        try {
            Class<?> mainClazz = Class.forName(className);
            if (!mainClazz.isAnnotationPresent(LrpcServiceScan.class)){
                log.error("@LrpcServiceScan annotation not found");
                throw new RuntimeException("@LrpcServiceScan annotation not found");
            }
            String[] scanPackages = mainClazz.getAnnotation(LrpcServiceScan.class).value();
            for (String scanPackage : scanPackages) {
                Reflections reflections = new Reflections(scanPackage);
                Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Deprecated.class);
                for (Class<?> clazz : classes) {
                    String serviceName = clazz.getAnnotation(LrpcService.class).value();
                    if (!clazz.isAnnotationPresent(LrpcServiceScan.class)){
                        log.error("@LrpcService annotation not found in service: {}", clazz.getSimpleName());
                    }
                    Object object;
                    try {
                        object = clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        log.error(e.getMessage());
                        continue;
                    }
                    if (StringUtils.isBlank(serviceName)) {
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            publishService(anInterface.getCanonicalName(), object);
                        }
                    }else {
                        publishService(serviceName, object);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("class {} not found", className);
            throw new RuntimeException("class" + className + " not found");
        }
    }

    private <T> void publishService(String serviceName, T service) {
        serviceProvider.addServiceProvider(serviceName, service);
    }

    /**
     * 启动RPC Server的方法
     */
    public void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new RequestMessageCodecSharable());
                    ch.pipeline().addLast(new RpcServerRequestMessageHandler());
                }
            });
            Channel channel = serverBootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
