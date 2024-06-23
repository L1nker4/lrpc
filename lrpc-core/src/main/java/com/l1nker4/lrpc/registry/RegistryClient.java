package com.l1nker4.lrpc.registry;

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

}
