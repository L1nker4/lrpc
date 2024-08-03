package com.l1nker4.lrpc.config;

import com.l1nker4.lrpc.registry.zookeeper.CuratorZookeeperClient;
import com.l1nker4.lrpc.registry.zookeeper.CuratorZookeeperClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/20
 */
@Slf4j
public class ZookeeperConfigCenter extends AbstractDynamicConfigCenter {

    private static final String CONFIG_ROOT_PATH = "/lrpc/config";

    private final CuratorZookeeperClient zookeeperClient;

    public ZookeeperConfigCenter(String address) {
        zookeeperClient = CuratorZookeeperClientFactory.getClients(address);
    }

    @Override
    protected void doInit() {
        List<String> configKeyList = zookeeperClient.getChildren(CONFIG_ROOT_PATH);
        if (CollectionUtils.isEmpty(configKeyList)) {
            log.info("ZookeeperConfigCenter configKeyList is empty");
            return;
        }
        for (String configKey : configKeyList) {
            String configValue = new String(zookeeperClient.getData(CONFIG_ROOT_PATH + "/" + configKey),
                    StandardCharsets.UTF_8);
            if (StringUtils.isBlank(configValue)) {
                continue;
            }
            Config.set(configKey, configValue);
        }
    }
}
