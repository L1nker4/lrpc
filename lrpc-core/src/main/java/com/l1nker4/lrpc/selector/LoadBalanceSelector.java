package com.l1nker4.lrpc.selector;

import com.l1nker4.lrpc.entity.ProviderService;

import java.util.List;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/6
 */
public interface LoadBalanceSelector {

    /**
     * select a service by the strategy
     * @param providerServiceList
     * @return
     */
    ProviderService select(List<ProviderService> providerServiceList);
}
