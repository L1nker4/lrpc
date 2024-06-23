package com.l1nker4.lrpc.registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {

    /**
     * register a service to center
     * @param serviceName serviceName
     * @param address service socket address
     */
    void registerService(String serviceName, InetSocketAddress address);
}
