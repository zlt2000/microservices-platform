package com.central.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期处理
 * 
 * @author zlt
 */
public class DateUtils {
    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }

	/** 时间格式(yyyy-MM-dd) */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static String format(Date date) {
        return format(date, DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }
}
