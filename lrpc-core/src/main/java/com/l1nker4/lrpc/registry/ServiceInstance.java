package com.l1nker4.lrpc.registry;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.Objects;
import lombok.*;

import java.io.Serializable;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/6/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class ServiceInstance implements Serializable {

    private String serviceName;

    private String host;

    private int port;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
