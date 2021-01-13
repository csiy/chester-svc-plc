package com.chester.svc.sys.util;

import java.util.Random;

public class RandomUtils {
    public static String randomHexString(int len) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            result.append(Integer.toHexString(new Random().nextInt(16)));
        }
        return result.toString().toUpperCase();
    }
}
