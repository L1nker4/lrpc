package com.l1nker4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ：L1nker4
 * @date ： 创建于  2024/03/02 16:52
 * @description： 常量类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloRequestEntity implements Serializable {

    private Integer id;
    private String message;

}
