package com.l1nker4.lrpc.config;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/20
 */
public abstract class AbstractDynamicConfigCenter implements DynamicConfigCenter {

    @Override
    public void initAllConfig() {
        doInit();
    }

    protected abstract void doInit();
}
