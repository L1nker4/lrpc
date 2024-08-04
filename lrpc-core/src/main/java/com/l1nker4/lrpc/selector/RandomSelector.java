package com.l1nker4.lrpc.selector;

import com.l1nker4.lrpc.entity.ProviderService;

import java.util.List;
import java.util.Random;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/7/7
 */
public class RandomSelector implements LoadBalanceSelector{

    private static final Random RANDOM = new Random();

    @Override
    public ProviderService select(List<ProviderService> providerServiceList) {
        return providerServiceList.get(RANDOM.nextInt(providerServiceList.size()));
    }
}
