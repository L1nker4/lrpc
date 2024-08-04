package com.l1nker4.lrpc.registry.nacos;

import com.l1nker4.lrpc.entity.ProviderService;
import com.l1nker4.lrpc.registry.AbstractServiceRegistry;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/6/23
 */
public class NacosServiceRegistry extends AbstractServiceRegistry {

    @Override
    public void registerService(ProviderService providerService) {

    }

    @Override
    public void unregisterService(ProviderService providerService) {

    }

    @Override
    public ProviderService getService(String servicePath) {
        return null;
    }

    @Override
    public void initServiceMap() {

    }
}
