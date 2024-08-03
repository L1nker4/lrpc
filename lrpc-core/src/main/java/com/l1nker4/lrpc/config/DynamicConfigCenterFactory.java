package com.l1nker4.lrpc.config;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/20
 */
public class DynamicConfigCenterFactory {

    private static DynamicConfigCenter INSTANCE;

    protected static final String CONFIG_CENTER_ADDRESS_KEY = "lrpc.registry.address";

    static {
        String configKey = (String) Config.getByName(CONFIG_CENTER_ADDRESS_KEY);
        String[] splitArr = configKey.split("://");
        if (splitArr.length != 2){
            throw new IllegalArgumentException("Invalid config key: " + configKey);
        }
        String protocol = splitArr[0];
        String address = splitArr[1];
        switch (protocol){
            case "zookeeper":
                INSTANCE = new ZookeeperConfigCenter(address);
                break;
            default:
                throw new IllegalArgumentException("Unknown protocol: " + protocol);
        }
    }

    public static DynamicConfigCenter getInstance() {
        return INSTANCE;
    }

}
