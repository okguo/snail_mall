package com.okguo.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Description: 该类是用来做日期格式化的
 * 用法：
 * 格式化
 * String dateText = DateFormatUtil.INSTANCE.format(date,  DateFormatUtil.yyyyMMddHHmmss_CN);
 * 解析
 * Date dateParse = DateFormatUtil.INSTANCE.parse(dateText,  DateFormatUtil.yyyyMMddHHmmss_CN);
 * 转换
 * DateFormatUtil.INSTANCE.transform(dateText, DateFormatUtil.yyyyMMddHHmmss, DateFormatUtil.yyyyMMddHHmm_H);
 */
public enum DateFormatUtil {

    INSTANCE;


    private final SimpleDateFormat mSdf;

    /*年月日时分秒*/

    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMddHHmmss_H = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmmss_CN = "yyyy年MM月dd日 HH:mm:ss";

    public static final String yyyyMMddHHmm = "yyyyMMddHHmm";
    public static final String yyyyMMddHHmm_H = "yyyy-MM-dd HH:mm";
    public static final String yyyyMMddHHmm_CN = "yyyy年MM月dd日 HH:mm";

    /*年月日*/

    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyyMMdd_H = "yyyy-MM-dd";
    public static final String yyyyMMdd_CN = "yyyy年MM月dd日";

    /*年月日时分*/

    public static final String MMddHHmm = "yyyyMMddHHmm";
    public static final String MMddHHmm_H = "MM-dd HH:mm";
    public static final String MMddHHmm_CN = "MM月dd日 HH:mm";

    /*月日*/

    public static final String MMdd = "MMdd";
    public static final String MMdd_H = "MM-dd";
    public static final String MMdd_CN = "MM月dd日";

    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";


    DateFormatUtil() {
        mSdf = new SimpleDateFormat();
    }

    public String format(Date date, String pattern) {
        mSdf.applyPattern(pattern);
        return mSdf.format(date);
    }

    public String format(long timeMillis, String pattern) {
        mSdf.applyPattern(pattern);
        return mSdf.format(new Date(timeMillis));
    }

    public String format(Calendar calendar, String pattern) {
        mSdf.applyPattern(pattern);
        return mSdf.format(calendar.getTime());
    }


    public Date parse(String timeText, String pattern) {
        Date parse = null;
        try {
            mSdf.applyPattern(pattern);
            parse = mSdf.parse(timeText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    public long parseToMillis(String timeText, String pattern) {
        return parse(timeText, pattern).getTime();
    }

    public Calendar parseToCalendar(String timeText, String pattern) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse(timeText, pattern));
        return calendar;
    }

    public String transform(String date, String patternIn, String patternOut) {
        return format(parse(date, patternIn), patternOut);
    }
}

