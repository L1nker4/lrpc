package com.l1nker4.lrpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：L1nker4
 * @date ： 创建于  2022/11/20 15:40
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseMessage {

    /**
     * 请求id
     */
    private String requestId;
}
