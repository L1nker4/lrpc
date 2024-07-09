package com.l1nker4.lrpc.registry.zookeeper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/6/22
 */
public class CuratorZookeeperClientFactory {

    private final static Map<String, CuratorZookeeperClient> clients = new ConcurrentHashMap<>();

    private final static Integer TIMEOUT = 1000 * 60;

    public static CuratorZookeeperClient getClients(String address) {
        if (clients.containsKey(address)){
            return clients.get(address);
        }else {
            CuratorZookeeperClient client = new CuratorZookeeperClient(address, TIMEOUT);
            clients.put(address, client);
            return client;
        }
    }
}
