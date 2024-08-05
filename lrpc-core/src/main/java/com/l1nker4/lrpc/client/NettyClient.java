package com.l1nker4.lrpc.client;

import com.l1nker4.lrpc.config.Config;
import com.l1nker4.lrpc.config.DynamicConfigCenter;
import com.l1nker4.lrpc.config.DynamicConfigCenterFactory;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.ProviderService;
import com.l1nker4.lrpc.entity.RpcRequest;
import com.l1nker4.lrpc.entity.RpcResponse;
import com.l1nker4.lrpc.registry.zookeeper.ZookeeperServiceRegistry;
import com.l1nker4.lrpc.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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

    private final NettyChannelPoolFactory nettyChannelPoolFactory;

    private final Class<? extends CommonSerializer> serializerClass = CommonSerializer.getClassByType(Config.getSerializerType());

    public NettyClient() {
        dynamicConfigCenter = DynamicConfigCenterFactory.getInstance();
        dynamicConfigCenter.initAllConfig();
        nettyChannelPoolFactory = NettyChannelPoolFactory.getInstance();
        String selectorStrategy = (String) Config.getByName(Constants.SELECTOR_STRATEGY);
        this.serviceRegistry = new ZookeeperServiceRegistry((String) Config.getByName(Constants.ZOOKEEPER_ADDRESS), selectorStrategy);
    }

    @Override
    public RpcResponse<?> sendRequest(RpcRequest request) {
        String servicePath = Constants.ROOT_PATH
                + Constants.SLASH
                + request.getGroupName()
                + Constants.SLASH
                + request.getInterfaceName()
                + Constants.SLASH + request.getVersion();

        ProviderService providerService = serviceRegistry.getService(servicePath);
        if (null == providerService) {
            throw new RuntimeException("no service found : " + servicePath);
        }
        String address = providerService.getAddress();
        if (StringUtils.isBlank(address)) {
            throw new RuntimeException("illegal service address");
        }
        Channel channel = null;
        try {
            channel = nettyChannelPoolFactory.getChannel(address, serializerClass, 5000);
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            channelFuture.syncUninterruptibly();
            return LrpcResponseHolder.get(request.getRequestId());
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
