package com.hsproduce.util;

public class StringUtil {

    public static boolean isNullOrEmpty(String s){
        return s == null ? true : s.length() == 0;
    }
    public static boolean isNullOrBlank(String s){
        return s == null ? true : s.trim().length() == 0;
    }
}
