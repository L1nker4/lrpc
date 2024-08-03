package com.l1nker4.lrpc.constants;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/19 22:52
 * @description： 常量类
 */
public class Constants {

    /**
     * request magic number
     */
    public static final byte[] MAGIC_NUMBER = {1, 2, 3, 4};
    public static final byte[] RETAIN_DATA = {0, 0};

    public static final byte VERSION = 1;

    public static final String ZOOKEEPER_ADDRESS = "zookeeper.address";

    public static final String SELECTOR_STRATEGY = "selector.strategy";

    public static final String ROOT_PATH = "/lrpc";

    public static final String SLASH = "/";

    public static final String DEFAULT_GROUP_NAME = "default-group";

    public static final String DEFAULT_VERSION = "1";
}
