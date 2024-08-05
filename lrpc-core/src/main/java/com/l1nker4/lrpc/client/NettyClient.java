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
import com.l1nker4.lrpc.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    private final ExecutorService fixedThreadPool;

    protected long timeoutMillis = 5000;

    private final Class<? extends CommonSerializer> serializerClass = CommonSerializer.getClassByType(Config.getSerializerType());

    public NettyClient() {
        dynamicConfigCenter = DynamicConfigCenterFactory.getInstance();
        dynamicConfigCenter.initAllConfig();
        String selectorStrategy = (String) Config.getByName(Constants.SELECTOR_STRATEGY);
        this.serviceRegistry = new ZookeeperServiceRegistry((String) Config.getByName(Constants.ZOOKEEPER_ADDRESS), selectorStrategy);
        this.fixedThreadPool = Executors.newFixedThreadPool(CommonUtils.getThreadConfigNumberOfIO());
    }

    @Override
    public RpcResponse<?> sendRequest(RpcRequest request) throws Exception {
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
        Future<RpcResponse<?>> future = fixedThreadPool
                .submit(LrpcResponseCallback
                        .builder()
                        .address(address)
                        .timeoutMillis(timeoutMillis)
                        .nettyChannelPoolFactory(NettyChannelPoolFactory.getInstance())
                        .request(request)
                        .build());
        return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
    }
}
