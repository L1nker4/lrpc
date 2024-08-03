package com.l1nker4.lrpc.selector;

import com.l1nker4.lrpc.entity.ProviderService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedRoundRobinLoadBalanceSelector implements LoadBalanceSelector {
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final AtomicInteger currentWeight = new AtomicInteger(0);

    @Override
    public ProviderService select(List<ProviderService> providerServiceList) {
        int totalWeight = providerServiceList.stream().mapToInt(ProviderService::getWeight).sum();
        while (true) {
            int index = currentIndex.getAndUpdate(i -> (i + 1) % providerServiceList.size());
            ProviderService service = providerServiceList.get(index);
            currentWeight.addAndGet(service.getWeight());
            if (currentWeight.get() >= totalWeight) {
                currentWeight.addAndGet(-totalWeight);
                return service;
            }
        }
    }
}