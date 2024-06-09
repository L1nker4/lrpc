package com.l1nker4.lrpc.provider;

public interface ServiceProvider {

    <T> void addServiceProvider(String serviceName, T service);

    Object getServiceProvider(String serviceName);
}
