package com.l1nker4.lrpc.registry.zookeeper;

import com.l1nker4.lrpc.registry.AbstractServiceRegistry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;

public class ZookeeperServiceRegistry extends AbstractServiceRegistry {

    private final CuratorZookeeperClient zookeeperClient;

    public ZookeeperServiceRegistry(String address) {
        this.zookeeperClient = CuratorZookeeperClientFactory.getClients(address);
    }

    @Override
    public void registerService(String serviceName, InetSocketAddress address) {
        zookeeperClient.create(serviceName + address.toString(), CreateMode.EPHEMERAL);
    }
}
