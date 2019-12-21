package com.hsproduce.util;

public class StringUtil {

    public static boolean isNullOrEmpty(String s){
        return s == null ? true : s.length() == 0;
    }
    public static boolean isNullOrBlank(String s){
        return s == null ? true : s.trim().length() == 0;
    }

    //转换为%E4%BD%A0形式  中文转url编码
    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = String.valueOf(c).getBytes("utf-8");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString().replace("/", "%2F").replaceAll(" ", "%20");
    }

}
