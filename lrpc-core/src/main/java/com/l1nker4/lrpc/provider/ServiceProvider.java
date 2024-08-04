package com.l1nker4.lrpc.provider;

import com.l1nker4.lrpc.entity.ProviderService;


public interface ServiceProvider {

    <T> void addServiceProvider(ProviderService providerService, T service);

    Object getServiceProvider(String serviceName);
}
