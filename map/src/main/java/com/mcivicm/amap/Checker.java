package com.mcivicm.amap;

import android.text.TextUtils;

/**
 * 检查助手
 */

public class Checker {
    public static void requireNoEmpty(String s, String paramName) {
        if (TextUtils.isEmpty(s)) {
            throw new IllegalArgumentException(paramName + "不能为空");
        }
    }

    public static void requireLengthPositive(String paramName, String... params) {
        if (params == null || params.length == 0) {
            throw new IllegalArgumentException(paramName + "的长度必须大于0");
        }
    }
}
