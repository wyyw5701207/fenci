package com.keywords.tokenizer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtils {
    /**
     * 将秒置为0
     * @param date
     * @return
     */
    public static Date formatToMinute(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    /**
     * 将分秒置为0
     * @param date
     * @return
     */
    public static Date formatToHour(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    /**
     * 将时分秒置为0
     * @param date
     * @return
     */
    public static Date formatToDay(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        // 将时分秒,毫秒域清零
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    /**
     * 格式化为 yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return sdf.format(date);
    }

    /**
     * 指定格式化
     * @param date
     * @param sdfStr
     * @return
     */
    public static String formatDate(Date date,String sdfStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(sdfStr);
        return sdf.format(date);
    }

    /**
     * 获取几天前的日期
     * @param dayNum 过去的天数，默认7天
     * @return
     */
    public static Date getDaysAgo(Integer dayNum) {
        if (null == dayNum)
            dayNum = 7;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -dayNum);
        return calendar.getTime();
    }
}
