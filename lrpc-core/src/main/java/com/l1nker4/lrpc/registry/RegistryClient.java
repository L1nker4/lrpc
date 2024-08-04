package com.l1nker4.lrpc.registry;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author ：L1nker4
 * @description: Registry Client
 * @date ： 创建于  2024/6/22
 */
public interface RegistryClient {

    void connect(String address, int timeout);

    /**
     * Create Znode in Zookeeper
     * @param path the path of the node
     * @param mode createMode
     */
    void create(String path, CreateMode mode);

    /**
     * create Znode with data in Zookeeper
     * @param path the path of the node
     * @param data data
     * @param mode createMode
     */
    void create(String path, byte[] data, CreateMode mode);

    /**
     *
     * @param path path
     * @return return true if the path exists
     */
    boolean exists(String path);

    /**
     * delete the Znode in Zookeeper
     * @param path the path of the node
     */
    void delete(String path);

    /**
     *  get the children of the path
     * @param path
     * @return
     */
    List<String> getChildren(String path);

    /**
     * get the data of the path
     * @param path
     * @return
     */
    byte[] getData(String path);

    /**
     * add watcher to the path
     * @param path
     * @param watcher
     */
    void addWatcher(String path, CuratorWatcher watcher);

    TreeCache registerTreeCache(String path);

    PathChildrenCache registerPathChildrenCache(String path);
}
