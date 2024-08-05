package com.l1nker4.lrpc.utils;

/**
 * @Author wanglin
 * @Description
 * @Date 2024-08-05 18:57
 */
public class CommonUtils {

    private static int CPU_NUM = Runtime.getRuntime().availableProcessors();

    private CommonUtils() {

    }

    public static int getThreadConfigNumberOfIO() {
        return CPU_NUM * 2 + 1;
    }


}
