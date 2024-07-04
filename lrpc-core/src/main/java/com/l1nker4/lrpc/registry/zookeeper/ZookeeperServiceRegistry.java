package com.l1nker4.lrpc.registry.zookeeper;

import com.google.common.collect.Lists;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.ProviderService;
import com.l1nker4.lrpc.registry.AbstractServiceRegistry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZookeeperServiceRegistry extends AbstractServiceRegistry {

    private final CuratorZookeeperClient zookeeperClient;

    //TODO Zookeeper watch
    private final Map<String, List<ProviderService>> providerServiceMap = new ConcurrentHashMap<>();

    public ZookeeperServiceRegistry(String address) {
        this.zookeeperClient = CuratorZookeeperClientFactory.getClients(address);
    }

    @Override
    public void registerService(ProviderService providerService) {
        if (!zookeeperClient.exists(Constants.ROOT_PATH)){
            zookeeperClient.create(Constants.ROOT_PATH, CreateMode.PERSISTENT);
        }
        String servicePath = providerService.getServicePath();
        List<ProviderService> serviceList = providerServiceMap.getOrDefault(servicePath, Lists.newArrayList());
        serviceList.add(providerService);
        providerServiceMap.put(servicePath, serviceList);
        zookeeperClient.create(Constants.ROOT_PATH + Constants.SLASH + servicePath,
                providerService.getAddress().getBytes(StandardCharsets.UTF_8),
                CreateMode.EPHEMERAL);
    }

    @Override
    public ProviderService getService(String servicePath) {
        List<ProviderService> providerServices = providerServiceMap.get(servicePath);
        if (CollectionUtils.isEmpty(providerServices)){
            return null;
        }else {
            //TODO selector
            return providerServices.get(0);
        }
    }

    @Override
    public void initServiceMap() {
        //TODO init map
    }
}
