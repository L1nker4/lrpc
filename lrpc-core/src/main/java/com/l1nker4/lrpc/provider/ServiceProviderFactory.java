package com.l1nker4.lrpc.provider;

public class ServiceProviderFactory {

    private static ServiceProvider serviceProvider;


    public static ServiceProvider getProvider() {
        synchronized (ServiceProviderFactory.class) {
            if(serviceProvider == null) {
                try {
                    serviceProvider = new DefaultServiceProvider();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return serviceProvider;
    }
}
