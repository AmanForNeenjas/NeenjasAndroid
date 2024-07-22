package com.hjq.demo.util;

import android.annotation.SuppressLint;
import android.os.ParcelFormatException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * @author The one
 * @date 2020/4/16 0016
 * @describe 日期工具类
 * @email 625805189@qq.com
 * @remark
 */
public class DateUtil {

    /**
     *           格式
     *
     *  yyyy    四位年
     *  yy      两位年
     *  MM      月份  始终两位
     *  M       月份
     *  dd      日期  始终两位
     *  d       日期
     *  HH      24小时制  始终两位
     *  H       24小时制
     *  hh      12小时制  始终两位
     *  h       12小时制
     *  mm      分钟  始终两位
     *  ss      秒  始终两位
     *
     */

    /**
     * 年月日 时分秒
     */
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 年月日
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 月日
     */
    public static final String MM_DD = "MM-dd";

    /**
     * 周
     */
    private static final String[] WEEKS = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    /**
     * 获取今天 年月日 格式的 String
     *
     * @return
     */
    public static String getTodayYMDString() {
        return dateToString(new Date(), YYYY_MM_DD);
    }

    /**
     * Date 转换成 年月日 格式的 String
     *
     * @param date
     * @return
     */
    public static String dateToYMDString(Date date) {
        return dateToString(date, YYYY_MM_DD);
    }

    /**
     * Date 转换成 String
     *
     * @param date   需要转换的日期
     * @param format 转换格式
     * @return
     */
    public static String dateToString(Date date, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String nowDateToString() {
        Date date = new Date();
        String format = "yyyy-MM-dd HH:mm:ss";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String nowDateToDevString() {
        Date date = new Date();
        String format = "yyyyMMdd";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Date 转换成  Calendar
     * @param date
     * @return
     */
    public static Calendar dateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Calendar 转换成 Date
     * @param date
     * @return
     */
    public static Date calendarToDate(Date date) {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    /**
     * string格式的日期转换成long类型
     * @param dateStr 需要转换的日期
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static long stringToLong(String dateStr) {
        return stringToLong(dateStr,YYYY_MM_DD);
    }

    /**
     * string格式的日期转换成long类型
     * @param dateStr 需要转换的日期
     * @param format 格式
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static long stringToLong(String dateStr, String format) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long nowToLong() {
        Date date = new Date();
        return date.getTime();
    }

    /**
     * string格式的日期转换成Date类型
     * @param dateStr 需要转换的日期
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String dateStr) {
        return stringToDate(dateStr,YYYY_MM_DD);
    }

    /**
     * string格式的日期转换成Date类型
     * @param dateStr 需要转换的日期
     * @param format 格式
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String dateStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ParcelFormatException(e.getMessage());
        }
    }

    /**
     * 获取 - 年
     * @param calendar
     * @return
     */
    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取 - 月
     * @param calendar
     * @return
     */
    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取 - 日
     * @param calendar
     * @return
     */
    public static int getDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取 - 周
     * @param calendar
     * @return
     */
    public static String getWeek(Calendar calendar) {
        return WEEKS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * @return 获取几天之内的的Date
     * @param amount 之后为整数 之前为负数
     */
    public static Date getNearestDayDate(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        return calendar.getTime();
    }

    /**
     * @return 获取几周之内的的Date
     * @param amount 之后为整数 之前为负数
     */
    public static Date getNearestWeekDate(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.WEEK_OF_YEAR, amount);
        return calendar.getTime();
    }

    /**
     * @return 获取几个月之内的日期
     * @param amount 之后为整数 之前为负数
     */
    public static Date getNearestMonthDate(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, amount);
        return calendar.getTime();
    }

    /**
     *
     * @return 获取几年之内的Date
     * @param amount 之后为整数 之前为负数
     */
    public static Date getNearestYearDate(int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, amount);
        return calendar.getTime();
    }

    /**
     * 获取某个年份的Date
     * @param year
     * @return
     */
    public static Date getYearDate(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTime();
    }

    /**
     * 获得两个日期间距多少天
     * @param beginDate
     * @return
     */
    public static int getTimeDistance(String beginDate){
        return getTimeDistance(stringToDate(beginDate),new Date());
    }

    /**
     * 获得两个日期间距多少天
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int getTimeDistance(String beginDate,String endDate){
        return getTimeDistance(stringToDate(beginDate),stringToDate(endDate));
    }

    /**
     * 获得两个日期间距多少天
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int getTimeDistance(Date beginDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(beginDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, fromCalendar.getMinimum(Calendar.HOUR_OF_DAY));
        fromCalendar.set(Calendar.MINUTE, fromCalendar.getMinimum(Calendar.MINUTE));
        fromCalendar.set(Calendar.SECOND, fromCalendar.getMinimum(Calendar.SECOND));
        fromCalendar.set(Calendar.MILLISECOND, fromCalendar.getMinimum(Calendar.MILLISECOND));

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, fromCalendar.getMinimum(Calendar.HOUR_OF_DAY));
        toCalendar.set(Calendar.MINUTE, fromCalendar.getMinimum(Calendar.MINUTE));
        toCalendar.set(Calendar.SECOND, fromCalendar.getMinimum(Calendar.SECOND));
        toCalendar.set(Calendar.MILLISECOND, fromCalendar.getMinimum(Calendar.MILLISECOND));

        int dayDistance = (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 3600 * 24));
        dayDistance = Math.abs(dayDistance);

        return dayDistance;
    }

    /**
     * Android 时间格式化（刚刚、x分钟前、x小时前、昨天、x天前、xx月xx日、xxxx年xx月xx日）
     * https://blog.csdn.net/d_shaoshuai/article/details/105067716?spm=1001.2101.3001.6650.5&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-5-105067716-blog-126143582.pc_relevant_recovery_v2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-5-105067716-blog-126143582.pc_relevant_recovery_v2&utm_relevant_index=9
     * @param timeMillis
     * @return
     */
    public static String format(long timeMillis) {
        return format(new Date(timeMillis));
    }

    /**
     * Android 时间格式化（刚刚、x分钟前、x小时前、昨天、x天前、xx月xx日、xxxx年xx月xx日）
     * @param timeMillis
     * @return
     */
    public static String format(String timeMillis) {
        return format(stringToDate(timeMillis,YYYY_MM_DD_HH_MM_SS));
    }

    public static String format(Date date) {
        Calendar calendar = Calendar.getInstance();
        //当前年
        int currYear = calendar.get(Calendar.YEAR);
        //当前日
        int currDay = calendar.get(Calendar.DAY_OF_YEAR);
        //当前时
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        //当前分
        int currMinute = calendar.get(Calendar.MINUTE);
        //当前秒
        int currSecond = calendar.get(Calendar.SECOND);

        calendar.setTime(date);
        int msgYear = calendar.get(Calendar.YEAR);
        //说明不是同一年
        if (currYear != msgYear) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        }
        int msgDay = calendar.get(Calendar.DAY_OF_YEAR);
        //超过7天，直接显示xx月xx日
        if (currDay - msgDay > 7) {
            return new SimpleDateFormat("MM-dd", Locale.getDefault()).format(date);
        }
        //不是当天
        if (currDay - msgDay > 0) {
            if (currDay - msgDay == 1) {
                return "昨天";
            } else {
                return currDay - msgDay + "天前";
            }
        }
        int msgHour = calendar.get(Calendar.HOUR_OF_DAY);
        int msgMinute = calendar.get(Calendar.MINUTE);
        //不是当前小时内
        if (currHour - msgHour > 0) {
            //如果当前分钟小，说明最后一个不满一小时
            if (currMinute < msgMinute) {
                if (currHour - msgHour == 1) {//当前只大一个小时值，说明不够一小时
                    return 60 - msgMinute + currMinute + "分钟前";
                } else {
                    return currHour - msgHour - 1 + "小时前";
                }
            }
            //如果当前分钟数大，够了一个周期
            return currHour - msgHour + "小时前";
        }
        int msgSecond = calendar.get(Calendar.SECOND);
        //不是当前分钟内
        if (currMinute - msgMinute > 0) {
            //如果当前秒数小，说明最后一个不满一分钟
            if (currSecond < msgSecond) {
                if (currMinute - msgMinute == 1) {//当前只大一个分钟值，说明不够一分钟
                    return "刚刚";
                } else {
                    return currMinute - msgMinute - 1 + "分钟前";
                }
            }
            //如果当前秒数大，够了一个周期
            return currMinute - msgMinute + "分钟前";
        }
        //x秒前
        return "刚刚";
    }

    /**
     * 获取当前时间前一个时间段内的随机时间
     * https://blog.csdn.net/Xx13624558575/article/details/123733341
     * @param beginDate
     * @param endDate
     * @return
     */
    public static Date randomDate(String beginDate, String endDate){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);

            if(start.getTime() >= end.getTime()){
                return null;
            }
            long date = random(start.getTime(),end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long random(long begin,long end) {
        long rtn = begin + (long) (Math.random() * (end - begin));
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }
}
