package com.hsproduce.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     *
     * @param date
     * @return yyyy-mm-dd
     */
    public static String format(Date date){
        String res = "";
        if(date == null){
            return res;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        res = sdf.format(date);
        return res;
    }
}
