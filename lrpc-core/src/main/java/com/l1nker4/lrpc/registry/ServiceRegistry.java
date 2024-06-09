package com.l1nker4.lrpc.registry;

public interface ServiceRegistry {

    /**
     * register a service
     * @param serviceName
     * @param service
     */
    void register(String serviceName, Object service);

    Object getService(String serviceName);
}
