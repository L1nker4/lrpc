package com.l1nker4.lrpc.selector;

import com.l1nker4.lrpc.entity.ProviderService;

import java.util.List;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/7
 */
public class RoundRobinSelector implements LoadBalanceSelector {

    private int index = 0;

    @Override
    public ProviderService select(List<ProviderService> providerServiceList) {
        if (index >= providerServiceList.size()) {
            index %= providerServiceList.size();
        }
        return providerServiceList.get(index++);
    }
}
