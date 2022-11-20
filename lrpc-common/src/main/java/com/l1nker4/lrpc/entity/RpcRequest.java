package com.l1nker4.lrpc.entity;

import lombok.*;

import java.io.Serializable;

/**
 * RPC请求类
 *
 * @author ：L1nker4
 * @date ： 创建于  2022/11/19 22:52
 * @description：
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest extends BaseMessage implements Serializable{


    /**
     * 调用接口名称
     */
    private String interfaceName;

    /**
     * 调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数
     */
    private Object[] parameters;

    /**
     * 调用方法的参数类型
     */
    private Class<?>[] paramTypes;

}
