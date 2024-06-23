package com.l1nker4.lrpc.discovery;

import java.net.InetSocketAddress;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/6/23
 */
public interface ServiceDiscovery {

    InetSocketAddress getServiceInstance(String serviceName);
}
