package com.yesido.lib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.Assert;

/**
 * 日期工具类
 * 
 * @author yesido
 * @date 2020年1月3日 上午9:59:37
 */
public class DateUtil extends DateUtils {

    public static final String Pattern_yyyy_MM_dd_HHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String Pattern_yyyy_MM_dd = "yyyy-MM-dd";
    public static final String Pattern_yyyy_MM = "yyyy-MM";
    public static final String Pattern_yyyy = "yyyy";
    public static final String Pattern_HHmmss = "HH:mm:ss";

    private DateUtil() {
        throw new RuntimeException("不允许初始化！");
    }

    public static void main(String[] args) throws ParseException {
        /*System.out.println(DateUtil.formateTime(getYearFirstDay(2014)));
        System.out.println(DateUtil.formateTime(getYearFirstDay(parseDate("2015-01-12", Pattern_yyyy_MM_dd))));
        
        System.out.println(DateUtil.formateTime(createDate(2014, 1, 20, 24, 1, 1)));
        
        System.out.println(DateUtil.formateTime(getYearLastDay(createDate(2014, 1, 20, 24, 1, 1))));
        
        System.out.println(DateUtil.formateTime(getClearCalendar().getTime()));*/

        System.out.println(DateUtil.formateTime(getCurrentMonthFirstDay()));
        System.out.println(DateUtil.formateTime(getMonthFirstDay(createDate(2014, 1, 20, 24, 1, 1))));
    }

    private static SimpleDateFormat getFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 获取Calendar：1970-01-01 00:00:00
     * 
     * @return Calendar
     */
    private static Calendar getClearCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        return calendar;
    }

    /**
     * 格式化日期：yyyy-MM-dd HH:mm:ss
     * 
     * @param date 日期
     * @return
     */
    public static String formateTime(Date date) {
        return formate(date, Pattern_yyyy_MM_dd_HHmmss);
    }

    /**
     * 格式化日期：yyyy-MM-dd
     * 
     * @param date 日期
     * @return
     */
    public static String formateDate(Date date) {
        return formate(date, Pattern_yyyy_MM_dd);
    }

    /**
     * 格式化日期
     * 
     * @param date 日期
     * @param pattern 格式
     * @return
     */
    public static String formate(Date date, String pattern) {
        return getFormat(pattern).format(date);
    }

    /**
     * 构造日期，默认00:00:00
     * 
     * @param year 年份：2014
     * @param month 月份：1-12
     * @param date 日期：1-31
     * @return
     */
    public static Date createDate(int year, int month, int date) {
        return createDate(year, month, date, 0, 0, 0);
    }

    /**
     * 构造日期
     * 
     * @param year year 年份：2014
     * @param month 月份：1-12【少于1或者大于12会相应往前/往后推月数】
     * @param date 日期：1-31【少于1或者大于31会相应往前/往后推天数】
     * @param hourOfDay 小时：0-23【少于0或者大于23会相应往前/往后推小时数】
     * @param minute 分钟：0-59【少于0或者大于59会相应往前/往后推分钟数】
     * @param second 秒数：0-59【少于0或者大于59会相应往前/往后推秒数】
     * @return
     */
    public static Date createDate(int year, int month, int date, int hourOfDay,
            int minute, int second) {
        Calendar calendar = getClearCalendar();
        month--;
        calendar.set(year, month, date, hourOfDay, minute, second);
        return calendar.getTime();
    }

    /**
     * 计算日期是今年的第几天
     * 
     * @param date 日期
     * @return 1=第一天
     */
    public static int getDayOfYear(Date date) {
        Assert.notNull(date, "日期不能为空");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 获取某年的第一天(00:00:00)
     * 
     * @param year 年份：2014
     * @return
     */
    public static Date getYearFirstDay(int year) {
        Calendar calendar = getClearCalendar();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取该日期下年份的第一天(00:00:00)
     * 
     * @param date 日期
     * @return
     */
    public static Date getYearFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currentYear = calendar.get(Calendar.YEAR);
        return getYearFirstDay(currentYear);
    }

    /**
     * 获取某年的最后一天(00:00:00)
     * 
     * @param year 年份：2014
     * @return
     */
    public static Date getYearLastDay(int year) {
        Calendar calendar = getClearCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        return currYearLast;
    }

    /**
     * 获取该日期下年份的最后一天(00:00:00)
     * 
     * @param date 日期
     * @return
     */
    public static Date getYearLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int currentYear = calendar.get(Calendar.YEAR);
        return getYearLastDay(currentYear);
    }

    /**
     * 是否是该日期下的年份的第一天
     * 
     * @param date 日期
     * @return true=是，false=否
     */
    public boolean isYearFirstDay(Date date) {
        return getDayOfYear(date) == 1;
    }

    /**
     * 获取当前月第一天(00:00:00)
     * 
     * @return
     */
    public static Date getCurrentMonthFirstDay() {
        return getMonthFirstDay(new Date());
    }

    /**
     * 获取某个日期当前月的第一天(00:00:00)
     * 
     * @param date 日期
     * @return
     */
    public static Date getMonthFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}
