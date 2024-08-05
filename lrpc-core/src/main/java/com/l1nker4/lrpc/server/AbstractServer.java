package com.l1nker4.lrpc.server;

import com.l1nker4.lrpc.annotation.LrpcService;
import com.l1nker4.lrpc.annotation.LrpcServiceScan;
import com.l1nker4.lrpc.config.Config;
import com.l1nker4.lrpc.config.DynamicConfigCenter;
import com.l1nker4.lrpc.config.DynamicConfigCenterFactory;
import com.l1nker4.lrpc.constants.Constants;
import com.l1nker4.lrpc.entity.ProviderService;
import com.l1nker4.lrpc.provider.ServiceProvider;
import com.l1nker4.lrpc.provider.ServiceProviderFactory;
import com.l1nker4.lrpc.registry.zookeeper.ZookeeperServiceRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.Set;

/**
 * @Author wanglin
 * @Description
 * @Date 2024-08-05 18:36
 */
@Slf4j
@Getter
public abstract class AbstractServer implements IServer {

    protected String host;
    protected int port;
    private final ServiceProvider serviceProvider;
    private final DynamicConfigCenter configCenter;

    //TODO multi registry
    private final ZookeeperServiceRegistry zookeeperServiceRegistry;

    public AbstractServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.serviceProvider = ServiceProviderFactory.getProvider();
        this.configCenter = DynamicConfigCenterFactory.getInstance();
        this.zookeeperServiceRegistry = new ZookeeperServiceRegistry((String) Config.getByName(Constants.ZOOKEEPER_ADDRESS)
                , (String) Config.getByName(Constants.SELECTOR_STRATEGY));
    }

    @Override
    public void init() {
        configCenter.initAllConfig();
        scanService();
        doInit();
    }

    @Override
    public void start() {
        //TODO optimize this invoke
        init();
        doStart(host, port);
    }

    @Override
    public void destroy() {
        doDestroy();
    }

    private void scanService() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = stackTrace[stackTrace.length - 1].getClassName();
        try {
            Class<?> mainClazz = Class.forName(className);
            if (!mainClazz.isAnnotationPresent(LrpcServiceScan.class)){
                log.error("@LrpcServiceScan annotation not found");
                throw new RuntimeException("@LrpcServiceScan annotation not found");
            }
            String[] scanPackages = mainClazz.getAnnotation(LrpcServiceScan.class).value();
            for (String scanPackage : scanPackages) {
                Reflections reflections = new Reflections(scanPackage);
                Set<Class<?>> classes = reflections.getTypesAnnotatedWith(LrpcService.class);
                for (Class<?> clazz : classes) {
                    LrpcService lrpcService = clazz.getAnnotation(LrpcService.class);
                    String serviceName = lrpcService.value();
                    if (!clazz.isAnnotationPresent(LrpcService.class)){
                        log.error("@LrpcService annotation not found in service: {}", clazz.getSimpleName());
                    }
                    Object object;
                    try {
                        object = clazz.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        log.error(e.getMessage());
                        continue;
                    }
                    ProviderService providerService = ProviderService.builder()
                            .weight(lrpcService.weight())
                            .groupName(lrpcService.group())
                            .version(lrpcService.version()).build();
                    if (StringUtils.isBlank(serviceName)) {
                        Class<?>[] interfaces = clazz.getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            providerService.setServiceName(anInterface.getCanonicalName());
                            publishService(providerService, object);
                        }
                    }else {
                        providerService.setServiceName(serviceName);
                        publishService(providerService, object);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.error("class {} not found", className);
            throw new RuntimeException("class" + className + " not found");
        }
    }

    private <T> void publishService(ProviderService providerService, T service) {
        providerService.setAddress(host + ":" + port);
        serviceProvider.addServiceProvider(providerService, service);
        zookeeperServiceRegistry.registerService(providerService);
    }

    public abstract void doInit();

    public abstract void doStart(String host, int port);

    public abstract void doDestroy();
}
