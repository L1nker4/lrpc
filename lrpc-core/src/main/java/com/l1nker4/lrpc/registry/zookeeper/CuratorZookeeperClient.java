package com.l1nker4.lrpc.registry.zookeeper;

import com.l1nker4.lrpc.registry.RegistryClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/6/22
 */
@Slf4j
public class CuratorZookeeperClient implements RegistryClient {

    private CuratorFramework client;

    private static final String ROOT_PATH = "/lrpc/";

    public CuratorZookeeperClient(String address, int timeout) {
        connect(address, timeout);
    }

    @Override
    public void connect(String address, int timeout) {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(address)
                .retryPolicy(new RetryNTimes(1, 1000))
                .connectionTimeoutMs(timeout)
                .sessionTimeoutMs(timeout);
        client = builder.build();
        client.start();
    }

    @Override
    public void create(String path, CreateMode mode) {
        try {
            client.create().creatingParentsIfNeeded().withMode(mode).forPath(ROOT_PATH + path);
        } catch (KeeperException.NodeExistsException e){
            log.error("the node already exists in the zookeeper", e);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (KeeperException.NoNodeException ignored) {
        }
        catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(ROOT_PATH + path);
        } catch (Exception e) {
            return null;
        }
    }

}
