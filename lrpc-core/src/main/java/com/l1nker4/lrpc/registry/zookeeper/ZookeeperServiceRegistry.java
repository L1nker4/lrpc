package com.l1nker4.lrpc.registry.zookeeper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.ProviderService;
import com.l1nker4.lrpc.registry.AbstractServiceRegistry;
import com.l1nker4.lrpc.selector.LoadBalanceFactory;
import com.l1nker4.lrpc.selector.LoadBalanceSelector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

@Slf4j
public class ZookeeperServiceRegistry extends AbstractServiceRegistry {

    private final CuratorZookeeperClient zookeeperClient;

    private final Map<String, List<ProviderService>> providerServiceMap = new ConcurrentHashMap<>();

    private final LoadBalanceSelector loadBalanceSelector;

    public ZookeeperServiceRegistry(String address, String loadBalanceStrategy) {
        this.zookeeperClient = CuratorZookeeperClientFactory.getClients(address);
        this.loadBalanceSelector = LoadBalanceFactory.getLoadBalanceSelector(loadBalanceStrategy);
        initServiceMap();
        registerWatcher();
    }

    private void registerWatcher() {
        TreeCache treeCache = zookeeperClient.registerTreeCache(Constants.ROOT_PATH);
        CountDownLatch latch = new CountDownLatch(1);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                treeCache.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            treeCache.getListenable().addListener(((client, event) -> {
                handleZookeeperEvent(event);
            }));
            // 添加钩子，以便在程序退出时释放资源
            Runtime.getRuntime().addShutdownHook(new Thread(latch::countDown));
            // 等待
            try {
                latch.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void handleZookeeperEvent(TreeCacheEvent event) {
        ChildData eventData = event.getData();
        if (eventData == null){
            return;
        }
        String path = eventData.getPath();
        if (!isValidPath(path)){
            log.info("node is not a valid path: {}", path);
            return;
        }
        byte[] data = eventData.getData();
        ProviderService providerService = JSON.to(ProviderService.class, JSONObject.parseObject(new String(data)));
        String mapKey = path.substring(0, path.lastIndexOf(Constants.SLASH));
        List<ProviderService> providerServiceList = providerServiceMap.get(mapKey);
        switch (event.getType()){
            case NODE_ADDED:
                if (!providerServiceList.contains(providerService)) {
                    log.info("add provider: {}", providerService);
                    providerServiceList.add(providerService);
                }
                break;
            case NODE_REMOVED:
                providerServiceList.remove(providerService);
                log.info("remove provider: {}", providerService);
                break;
            case CONNECTION_LOST:
            default:
                log.info("event type:{}", event.getType());
        }
    }

    private boolean isValidPath(String path) {
        String lastElement = path.substring(path.lastIndexOf("/") + 1);
        if (StringUtils.isBlank(lastElement)) {
            return false;
        }
        String[] split = lastElement.split(":");
        return split.length == 2;
    }

    @Override
    public void registerService(ProviderService providerService) {
        if (!zookeeperClient.exists(Constants.ROOT_PATH)){
            zookeeperClient.create(Constants.ROOT_PATH, CreateMode.PERSISTENT);
        }
        String servicePath = providerService.getServicePath();
        List<ProviderService> serviceList = providerServiceMap.getOrDefault(servicePath, Lists.newArrayList());
        serviceList.add(providerService);

        providerServiceMap.put(Constants.ROOT_PATH + Constants.SLASH + servicePath, serviceList);
        String baseServicePath = Constants.ROOT_PATH
                + Constants.SLASH
                + servicePath
                + Constants.SLASH
                + providerService.getAddress();

        zookeeperClient.create(baseServicePath,
                JSON.toJSONString(providerService).getBytes(StandardCharsets.UTF_8),
                CreateMode.EPHEMERAL);
    }

    @Override
    public void unregisterService(ProviderService providerService) {
        if (!zookeeperClient.exists(Constants.ROOT_PATH)){
            throw new IllegalStateException("Zookeeper service not exists");
        }
        String servicePath = providerService.getServicePath();
        String baseServicePath = Constants.ROOT_PATH
                + Constants.SLASH
                + servicePath
                + Constants.SLASH
                + providerService.getAddress();
        zookeeperClient.delete(baseServicePath);
        log.info("unregister service: {}", providerService);
    }

    @Override
    public ProviderService getService(String servicePath) {
        List<ProviderService> providerServices = providerServiceMap.get(servicePath);
        if (CollectionUtils.isEmpty(providerServices)){
            return null;
        }else {
            return loadBalanceSelector.select(providerServices);
        }
    }

    @Override
    public void initServiceMap() {
        List<String> groupNameList = zookeeperClient.getChildren(Constants.ROOT_PATH);
        if (CollectionUtils.isEmpty(groupNameList)){
            log.info("zookeeper group name list is empty");
            return;
        }
        for (String groupName : groupNameList) {
            String groupPath = Constants.ROOT_PATH + Constants.SLASH + groupName;
            List<String> currGroupChildList = zookeeperClient.getChildren(groupPath);
            if (CollectionUtils.isEmpty(currGroupChildList)){
                log.info("zookeeper group child list is empty");
                return;
            }
            for (String serviceName : currGroupChildList) {
                String servicePath = groupPath + Constants.SLASH + serviceName;
                List<String> versionList = zookeeperClient.getChildren(servicePath);
                if (CollectionUtils.isEmpty(versionList)){
                    log.info("zookeeper service version list is empty");
                    return;
                }
                for (String version : versionList) {
                    String versionPath = servicePath + Constants.SLASH + version;
                    List<String> providerList = zookeeperClient.getChildren(versionPath);
                    if (CollectionUtils.isEmpty(providerList)){
                        log.info("zookeeper service provider list is empty");
                        return;
                    }
                    for (String providerAddress : providerList) {
                        String providerPath = versionPath + Constants.SLASH + providerAddress;
                        byte[] data = zookeeperClient.getData(providerPath);
                        try {
                            ProviderService providerService = JSON.to(ProviderService.class, JSONObject.parseObject(new String(data)));
                            List<ProviderService> serviceList = providerServiceMap.getOrDefault(versionPath, Lists.newArrayList());
                            serviceList.add(providerService);
                            providerServiceMap.put(versionPath, serviceList);
                        }catch (Exception e){
                            log.error("serialize zookeeper service error", e);
                        }
                    }
                }
            }
        }
    }
}
