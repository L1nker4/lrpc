package com.l1nker4.lrpc.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServiceRegistry.class);

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    @Override
    public void register(String serviceName, Object service) {
        logger.info("Register service: {}", serviceName);
        serviceMap.put(serviceName, service);
    }

    @Override
    public Object getService(String serviceName) {
        return serviceMap.getOrDefault(serviceName, null);
    }


}
