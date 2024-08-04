package com.l1nker4.lrpc.selector;

import com.l1nker4.lrpc.entity.ProviderService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashingLoadBalanceSelector implements LoadBalanceSelector {

    private final SortedMap<Long, ProviderService> circle = new TreeMap<>();
    private int numberOfReplicas;

    public ConsistentHashingLoadBalanceSelector() {

    }

    private void add(ProviderService service) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hash(service.getAddress() + i), service);
        }
    }

    private void remove(ProviderService service) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hash(service.getAddress() + i));
        }
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
            return ((long) (digest[0] & 0xFF) << 24) |
                   ((long) (digest[1] & 0xFF) << 16) |
                   ((long) (digest[2] & 0xFF) << 8) |
                   (digest[3] & 0xFF);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProviderService select(List<ProviderService> providerServiceList) {
        this.numberOfReplicas = providerServiceList.size();
        for (ProviderService service : providerServiceList) {
            add(service);
        }

        long hash = hash(String.valueOf(System.currentTimeMillis()));  // or use other unique key
        if (!circle.containsKey(hash)) {
            SortedMap<Long, ProviderService> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }
}
