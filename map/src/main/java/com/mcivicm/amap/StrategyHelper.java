package com.mcivicm.amap;

/**
 * 驾驶策略助手
 */

class StrategyHelper {

    private static int[] singles = new int[]{0, 1, 2, 3, 4, 6, 7, 8, 9};

    static void requireSingle(int strategy, String message) {
        for (int s : singles) {
            if (s == strategy) {
                return;
            }
        }
        throw new IllegalArgumentException(message);
    }

    static void requireMulti(int s, String msg) {
        for (int single : singles) {
            if (single == s) {
                throw new IllegalArgumentException(msg);
            }
        }
    }
}
