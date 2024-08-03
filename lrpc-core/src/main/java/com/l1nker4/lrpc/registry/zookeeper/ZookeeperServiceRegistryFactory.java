package com.l1nker4.lrpc.registry.zookeeper;

import com.l1nker4.lrpc.config.Config;
import com.l1nker4.lrpc.constants.Constants;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/6
 */
public class ZookeeperServiceRegistryFactory {

    private static ZookeeperServiceRegistry serviceRegistry;

    private ZookeeperServiceRegistryFactory() {}

    public static ZookeeperServiceRegistry getZookeeperServiceRegistry(String address) {
        String selectorStrategy = (String) Config.getByName(Constants.SELECTOR_STRATEGY);
        if (serviceRegistry == null) {
            synchronized (ZookeeperServiceRegistry.class) {
                if (serviceRegistry == null) {
                    serviceRegistry = new ZookeeperServiceRegistry(address, selectorStrategy);
                }
            }
        }
        return serviceRegistry;
    }
}
