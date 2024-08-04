package com.l1nker4.lrpc.selector;

import com.l1nker4.lrpc.entity.ProviderService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoundRobinSelectorTest {

    @Test
    public void testSelect() {
        RoundRobinSelector selector = new RoundRobinSelector();
        ProviderService service1 = new ProviderService("service1", "group1", "v1", 1, "address1");
        ProviderService service2 = new ProviderService("service2", "group1", "v1", 2, "address2");
        ProviderService service3 = new ProviderService("service3", "group1", "v1", 3, "address3");
        ProviderService service4 = new ProviderService("service4", "group1", "v1", 4, "address4");
        ProviderService service5 = new ProviderService("service5", "group1", "v1", 5, "address5");
        ProviderService service6 = new ProviderService("service6", "group1", "v1", 2, "address6");
        List<ProviderService> services = Arrays.asList(service1, service2, service3, service4, service5, service6);

        assertEquals(service1, selector.select(services));
        assertEquals(service2, selector.select(services));
        assertEquals(service3, selector.select(services));
        assertEquals(service4, selector.select(services));
        assertEquals(service5, selector.select(services));
        assertEquals(service6, selector.select(services));
        assertEquals(service1, selector.select(services)); // should loop back
    }
}
