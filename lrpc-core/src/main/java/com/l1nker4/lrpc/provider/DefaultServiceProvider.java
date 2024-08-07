package com.l1nker4.lrpc.provider;

import com.l1nker4.lrpc.entity.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceProvider implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceProvider.class);

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    @Override
    public <T> void addServiceProvider(ProviderService providerService, T service) {
        serviceMap.put(providerService.getServiceName(), service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), providerService.toString());
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RuntimeException("service not found: " + serviceName);
        }
        return service;
    }
}
