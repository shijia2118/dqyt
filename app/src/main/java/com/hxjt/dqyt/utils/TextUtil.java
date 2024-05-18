package com.hxjt.dqyt.utils;

public class TextUtil {

    // 自定义的比较方法，忽略大小写
    public static boolean isEqualIgnoreCase(String str1, String str2) {
        // 如果两个字符串相同，则直接返回 true
        if (str1.equals(str2)) {
            return true;
        }
        // 如果两个字符串长度不相同，则肯定不相等
        if (str1.length() != str2.length()) {
            return false;
        }
        // 遍历字符串，逐个字符比较（忽略大小写）
        for (int i = 0; i < str1.length(); i++) {
            char ch1 = Character.toLowerCase(str1.charAt(i));
            char ch2 = Character.toLowerCase(str2.charAt(i));
            if (ch1 != ch2) {
                return false; // 如果有任何字符不相同，则返回 false
            }
        }
        // 如果所有字符都相同，则返回 true
        return true;
    }
}
