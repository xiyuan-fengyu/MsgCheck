package com.xiyuan.template.util;

/**
 * Created by xiyuan_fengyu on 2016/8/9.
 */
public class RandomUtil {

    public static int randomBetween(int start, int end) {
        if (start == end) {
            return start;
        }
        else {
            int between = Math.abs(start - end) + 1;
            return Math.min(start, end) + (int) (Math.random() * between);
        }
    }

    public static String randomStrFrom(String chars, int len) {
        if (chars == null || chars.isEmpty() || len <= 0) {
            return "";
        }

        int charLen = chars.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt((int) (charLen * Math.random())));
        }
        return sb.toString();
    }

    private static final String colorChars = "0123456789abcdef";
    public static String randomColorStr() {
        return "#" + randomStrFrom(colorChars, 6);
    }

    private static final String numAndLetterChars = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomCode(int len) {
        return randomStrFrom(numAndLetterChars, len);
    }

}
