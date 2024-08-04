package com.l1nker4.lrpc.enumeration;

/**
 * 状态码-枚举类
 *
 * @author l1nker4
 */

public enum ResponseCode {

    /**
     *
     */
    SUCCESS(200, "请求成功"),
    FAILED(400, "请求失败")
    ;


    private Integer code;

    private String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
