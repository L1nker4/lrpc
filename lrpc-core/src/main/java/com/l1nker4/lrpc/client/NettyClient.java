package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.config.Config;
import com.l1nker4.lrpc.config.DynamicConfigCenter;
import com.l1nker4.lrpc.config.DynamicConfigCenterFactory;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.ProviderService;
import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.entity.RpcResponse;
import com.l1nker4.lrpc.handler.RpcClientResponseMessageHandler;
import com.l1nker4.lrpc.protocol.ProtocolFrameDecoder;
import com.l1nker4.lrpc.protocol.ResponseMessageCodecSharable;
import com.l1nker4.lrpc.registry.zookeeper.ZookeeperServiceRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Netty客户端实现
 *
 * @author ：L1nker4
 * @date ： 创建于  2024/3/3 15:41
 */
@Slf4j
public class NettyClient implements RpcClient {

    private final ZookeeperServiceRegistry serviceRegistry;

    private final DynamicConfigCenter dynamicConfigCenter;

    public NettyClient() {
        dynamicConfigCenter = DynamicConfigCenterFactory.getInstance();
        dynamicConfigCenter.initAllConfig();

        String selectorStrategy = (String) Config.getByName(Constants.SELECTOR_STRATEGY);
        this.serviceRegistry = new ZookeeperServiceRegistry((String) Config.getByName(Constants.ZOOKEEPER_ADDRESS), selectorStrategy);
    }

    @Override
    public RpcResponse<?> sendRequest(RpcRequest request) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);

            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ResponseMessageCodecSharable());
                    ch.pipeline().addLast(new RpcClientResponseMessageHandler(responseFuture));
                }
            });

            String servicePath = Constants.ROOT_PATH
                    + Constants.SLASH
                    + request.getGroupName()
                    + Constants.SLASH
                    + request.getInterfaceName()
                    + Constants.SLASH + request.getVersion();

            ProviderService providerService = serviceRegistry.getService(servicePath);
            String address = providerService.getAddress();
            if (StringUtils.isBlank(address)) {
                throw new RuntimeException("illegal service address");
            }


            String[] splitStr = address.split(":");
            Channel channel = bootstrap.connect(splitStr[0], Integer.parseInt(splitStr[1])).sync().channel();
            log.info("rpc client request : {}", request);
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            channelFuture.addListener((ChannelFutureListener) promise -> {
                if (!promise.isSuccess()) {
                    promise.channel().close();
                    log.error("发送消息时有错误发生: ", promise.cause());
                } else {
                    log.info("发送消息成功");
                }
            });
            channelFuture.awaitUninterruptibly();
            // 等待响应
            return responseFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }
}
