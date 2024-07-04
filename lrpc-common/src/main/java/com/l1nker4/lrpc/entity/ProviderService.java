package com.l1nker4.lrpc.entity;

import com.l1nker4.lrpc.constants.Constants;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ：L1nker4
 * @description:
 * @date ： 创建于  2024/6/28
 */
@Data
@Builder
public class ProviderService implements Serializable {

    private String serviceName;

    private String groupName;

    private String version;

    private Integer weight;

    private String address;

    public String getServicePath(){
        return groupName+ Constants.SLASH + serviceName + Constants.SLASH + version;
    }
}
