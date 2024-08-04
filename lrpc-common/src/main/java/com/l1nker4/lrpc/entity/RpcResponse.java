package com.l1nker4.lrpc.entity;

import com.l1nker4.lrpc.enumeration.ResponseCode;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * RPC响应类
 *
 * @author ：L1nker4
 * @date ： 创建于  2022/11/19 22:57
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class RpcResponse<T> extends BaseMessage implements Serializable {

    private ResponseCode code;

    private T data;

}
