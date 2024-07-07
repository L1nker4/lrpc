package com.l1nker4.lrpc.registry.zookeeper;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/6
 */
public class ZookeeperServiceRegistryFactory {

    private static ZookeeperServiceRegistry serviceRegistry;

    private ZookeeperServiceRegistryFactory() {}

    public static ZookeeperServiceRegistry getZookeeperServiceRegistry(String address) {
        if (serviceRegistry == null) {
            synchronized (ZookeeperServiceRegistry.class) {
                if (serviceRegistry == null) {
                    serviceRegistry = new ZookeeperServiceRegistry(address);
                }
            }
        }
        return serviceRegistry;
    }
}
