package com.l1nker4.lrpc.entity;

import com.l1nker4.lrpc.constants.Constants;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * RPC请求类
 *
 * @author ：L1nker4
 * @date ： 创建于  2022/11/19 22:52
 * @description：
 */

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RpcRequest extends BaseMessage implements Serializable {


    /**
     * 调用接口名称
     */
    private String interfaceName;

    /**
     * 接口组
     */
    private String groupName;

    /**
     * 接口版本号
     */
    private String version;

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

    public String getServicePath() {
        return Constants.ROOT_PATH
                + Constants.SLASH
                + groupName
                + Constants.SLASH
                + interfaceName
                + Constants.SLASH + version;
    }

}
