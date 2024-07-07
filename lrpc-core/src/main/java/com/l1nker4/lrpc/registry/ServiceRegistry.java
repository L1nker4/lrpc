package com.l1nker4.lrpc.registry;

import com.l1nker4.lrpc.entity.ProviderService;


public interface ServiceRegistry {

    /**
     * register a service to center
     * @param serviceProvider serviceProvider
     */
    void registerService(ProviderService serviceProvider);

    void unregisterService(ProviderService providerService);

    /**
     * get a service instance
     * @param servicePath
     * @return
     */
    ProviderService getService(String servicePath);

    void initServiceMap();
}
