package com.l1nker4.lrpc.registry.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/6
 */
@Slf4j
public class LrpcServiceListener implements CuratorWatcher {

    private ZookeeperServiceRegistry zookeeperServiceRegistry;

    public LrpcServiceListener(ZookeeperServiceRegistry zookeeperServiceRegistry) {
        this.zookeeperServiceRegistry = zookeeperServiceRegistry;
    }


    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        switch (watchedEvent.getType()) {
            case NodeCreated:
                log.info("Create zookeeper service, path: {}, status: {}", watchedEvent.getPath(), watchedEvent.getState());
                break;
            case NodeDeleted:
                log.info("Delete zookeeper service,  path: {}, data: {}", watchedEvent.getPath(), watchedEvent.getState());
                break;
            default:

        }
    }
}
