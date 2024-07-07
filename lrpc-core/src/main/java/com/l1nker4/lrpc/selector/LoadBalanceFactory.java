package com.l1nker4.lrpc.selector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/7
 */
public class LoadBalanceFactory {

    private static final Map<String, LoadBalanceSelector> loadBalanceSelectorMap = new ConcurrentHashMap<>();

    static {
        loadBalanceSelectorMap.put("random", new RandomSelector());
        loadBalanceSelectorMap.put("roundRobin", new RoundRobinSelector());
    }

    public static LoadBalanceSelector getLoadBalanceSelector(String name) {
        return loadBalanceSelectorMap.get(name);
    }
}
