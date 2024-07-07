package com.l1nker4.lrpc.registry.zookeeper;

import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.registry.RegistryClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheBuilder;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
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
            client.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
        } catch (KeeperException.NodeExistsException e){
            log.error("the node already exists in the zookeeper", e);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void create(String path, byte[] data, CreateMode mode) {
        try {
            client.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data);
        } catch (KeeperException.NodeExistsException e){
            log.error("the node already exists in the zookeeper", e);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean exists(String path) {
        Stat stat;
        try {
             stat = client.checkExists().forPath(path);
        }catch (Exception e){
            throw new IllegalStateException(e);
        }
        return stat != null;
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
            return client.getChildren().forPath(path);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public byte[] getData(String path) {
        try {
            return client.getData().forPath(path);
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addWatcher(String path, CuratorWatcher watcher) {
        try {
            client.getData().usingWatcher(watcher).forPath(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TreeCache registerTreeCache(String path) {
        return new TreeCache(client, path);
    }

    @Override
    public PathChildrenCache registerPathChildrenCache(String path) {
        return new PathChildrenCache(client, path, true);
    }

}
