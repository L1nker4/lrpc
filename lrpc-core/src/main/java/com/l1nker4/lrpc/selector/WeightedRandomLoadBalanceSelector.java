package com.l1nker4.lrpc.selector;

import com.l1nker4.lrpc.entity.ProviderService;

import java.util.List;
import java.util.Random;

public class WeightedRandomLoadBalanceSelector implements LoadBalanceSelector {
    private final Random random = new Random();

    @Override
    public ProviderService select(List<ProviderService> providerServiceList) {
        int totalWeight = providerServiceList.stream().mapToInt(ProviderService::getWeight).sum();
        int randomWeight = random.nextInt(totalWeight);
        for (ProviderService service : providerServiceList) {
            randomWeight -= service.getWeight();
            if (randomWeight < 0) {
                return service;
            }
        }
        return null;  // This should never happen
    }
}