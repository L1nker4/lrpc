package com.l1nker4.lrpc.server;

import com.l1nker4.lrpc.annotation.LrpcService;
import com.l1nker4.lrpc.annotation.LrpcServiceScan;
import com.l1nker4.lrpc.config.Config;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.ProviderService;
import com.l1nker4.lrpc.handler.RpcServerRequestMessageHandler;
import com.l1nker4.lrpc.protocol.ProtocolFrameDecoder;
import com.l1nker4.lrpc.protocol.RequestMessageCodecSharable;
import com.l1nker4.lrpc.provider.ServiceProvider;
import com.l1nker4.lrpc.provider.ServiceProviderFactory;
import com.l1nker4.lrpc.registry.zookeeper.ZookeeperServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * RPC Server
 *
 * @author l1nker4
 */
@Slf4j
public class RpcServer {

    private final String host;

    private final int port;

    private final ServiceProvider serviceProvider;

    private final ZookeeperServiceRegistry zookeeperServiceRegistry;

    public RpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.serviceProvider = ServiceProviderFactory.getProvider();
        String selectorStrategy = (String) Config.getByName(Config.SELECTOR_STRATEGY);
        this.zookeeperServiceRegistry = new ZookeeperServiceRegistry((String) Config.getByName(Constants.ZOOKEEPER_ADDRESS), selectorStrategy);
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
                Set<Class<?>> classes = reflections.getTypesAnnotatedWith(LrpcService.class);
                for (Class<?> clazz : classes) {
                    LrpcService lrpcService = clazz.getAnnotation(LrpcService.class);
                    String serviceName = lrpcService.value();
                    if (!clazz.isAnnotationPresent(LrpcService.class)){
                        log.error("@LrpcService annotation not found in service: {}", clazz.getSimpleName());
                    }
                    Object object;
                    try {
                        object = clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        log.error(e.getMessage());
                        continue;
                    }
                    ProviderService providerService = ProviderService.builder()
                            .weight(lrpcService.weight())
                            .groupName(lrpcService.group())
                            .version(lrpcService.version()).build();
                    if (StringUtils.isBlank(serviceName)) {
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            providerService.setServiceName(anInterface.getCanonicalName());
                            publishService(providerService, object);
                        }
                    }else {
                        providerService.setServiceName(serviceName);
                        publishService(providerService, object);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("class {} not found", className);
            throw new RuntimeException("class" + className + " not found");
        }
    }

    private <T> void publishService(ProviderService providerService, T service) {
        providerService.setAddress(host + ":" + port);
        serviceProvider.addServiceProvider(providerService, service);
        zookeeperServiceRegistry.registerService(providerService);
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

            //// keeplive and deny nagle
            serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true);
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
