package com.l1nker4.lrpc.selector;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/7
 */
@Slf4j
public class LoadBalanceFactory {

    private static final Map<String, Class<? extends LoadBalanceSelector>> loadBalanceSelectorMap = new ConcurrentHashMap<>();

    private static final String DEFAULT_SELECTOR = "random";

    static {
        loadBalanceSelectorMap.put("random", RandomSelector.class);
        loadBalanceSelectorMap.put("roundRobin", RoundRobinSelector.class);
        loadBalanceSelectorMap.put("weightedRandom", WeightedRandomLoadBalanceSelector.class);
        loadBalanceSelectorMap.put("weightRoundRobin", WeightedRoundRobinLoadBalanceSelector.class);
        loadBalanceSelectorMap.put("consistentHash", ConsistentHashingLoadBalanceSelector.class);
    }

    public static LoadBalanceSelector getLoadBalanceSelector(String name) {

        try {
            if (loadBalanceSelectorMap.containsKey(name)) {
                return loadBalanceSelectorMap.get(name).newInstance();
            } else {
                return loadBalanceSelectorMap.get(DEFAULT_SELECTOR).newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("loadBalanceSelectorMap get error");
        }
    }
}
