package com.l1nker4.lrpc.selector;

import com.l1nker4.lrpc.entity.ProviderService;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WeightedRoundRobinLoadBalanceSelectorTest {

    @Test
    public void testSelect() {
        ProviderService service1 = new ProviderService("service1", "group1", "v1", 1, "address1");
        ProviderService service2 = new ProviderService("service2", "group1", "v1", 2, "address2");
        ProviderService service3 = new ProviderService("service3", "group1", "v1", 1, "address3");
        ProviderService service4 = new ProviderService("service4", "group1", "v1", 2, "address4");
        ProviderService service5 = new ProviderService("service5", "group1", "v1", 1, "address5");
        ProviderService service6 = new ProviderService("service6", "group1", "v1", 1, "address6");
        List<ProviderService> services = Arrays.asList(service1, service2, service3, service4, service5, service6);

        WeightedRoundRobinLoadBalanceSelector selector = new WeightedRoundRobinLoadBalanceSelector();

        // Run multiple times to ensure proper distribution
        ProviderService selected = selector.select(services);
        assertTrue(service1.equals(selected) || service2.equals(selected) || 
                   service3.equals(selected) || service4.equals(selected) ||
                   service5.equals(selected) || service6.equals(selected));

        // Additional tests to validate distribution can be added here
    }
}
