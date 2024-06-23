package com.l1nker4.lrpc.discovery;

import com.l1nker4.lrpc.registry.zookeeper.CuratorZookeeperClient;
import com.l1nker4.lrpc.registry.zookeeper.CuratorZookeeperClientFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/6/23
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private final CuratorZookeeperClient zookeeperClient;

    public ZookeeperServiceDiscovery(String address) {
        this.zookeeperClient = CuratorZookeeperClientFactory.getClients(address);
    }

    @Override
    public InetSocketAddress getServiceInstance(String serviceName) {
        List<String> children = zookeeperClient.getChildren(serviceName);
        if (children == null || children.isEmpty()) {
            return null;
        }
        return new InetSocketAddress(children.get(0), 8080);
    }
}
