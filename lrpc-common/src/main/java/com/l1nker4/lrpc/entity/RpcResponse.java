package com.l1nker4.lrpc.entity;

import com.l1nker4.lrpc.enumeration.ResponseCode;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Calendar;

/**
 * RPC响应类
 *
 * @author ：L1nker4
 * @date ： 创建于  2022/11/19 22:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class RpcResponse<T> extends BaseMessage implements Serializable {

    private ResponseCode code;

    private T data;


    public static <T> RpcResponse<T> success(T data, String requestId) {
        return RpcResponse.<T>builder()
                .code(ResponseCode.SUCCESS)
                .data(data).build();
    }

    public static <T> RpcResponse<T> fail(T data, String requestId) {
        return RpcResponse.<T>builder()
                .code(ResponseCode.FAILED)
                .data(data).build();
    }



}
