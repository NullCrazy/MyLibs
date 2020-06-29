package com.lucky.utils.date;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 描述：日历工具类
 *
 * @author tengxs
 * @version 1.0
 */
public class CalendarUtil {
    /**
     * 取得当月月份(1,2,3...10,11,12)
     *
     * @return int
     */
    public static String getCurMonth() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.MONTH) + 1);
    }

    /**
     * 取得当月的上个月(1,2,3...10,11,12),若当月是1月上月则是12月
     *
     * @return int
     */
    public static int getLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 根据日期（yyyy-MM-dd)取得日期是该年的第几周
     *
     * @return String
     */
    public static String getWeekByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
    }

    /**
     * 取得当月的上个月所在的年份
     *
     * @return int
     */
    public static int getYearByMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 取得时间所在年份（4位数字表示）
     *
     * @return String
     */

    public static String getCurrentYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    /**
     * 当前时间前几周的开始时间和结束时间
     *
     * @throws Exception
     */
    public static String[] getDivWeekDate(int weeks) throws Exception {
        try {
            String[] retStr = new String[2];
            Date curDate = new Date();
            Calendar cal = Calendar.getInstance();
            Date preDate = CalendarUtil.subtructTime(curDate, (long) weeks * 7 * 24 * 60 * 60 * 1000);
            String preWeek = CalendarUtil.getWeekByDate(preDate);
            cal.setTime(preDate);
            String startDate = CalendarUtil.getFirstDayByWeek(CalendarUtil.getCurrentYear(preDate), preWeek);
            String endDate = CalendarUtil.getLastDayByWeek(CalendarUtil.getCurrentYear(preDate), preWeek);
            retStr[0] = startDate;
            retStr[1] = endDate;
            return retStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 当前时间的的前几个月开始时间和结束时间。 months 取值 从1到12
     *
     * @throws Exception
     */
    public static String[] getDivMonthDate(int months) throws Exception {
        try {
            String[] retStr = new String[2];
            Calendar cal = Calendar.getInstance();
            int curMonth = cal.get(Calendar.MONTH) + 1;
            int curYear = cal.get(Calendar.YEAR);
            if ((curMonth - months) <= 0) {
                curYear -= 1;
            }
            int divMonth = (curMonth - months + 12) % 12;
            if (divMonth == 0) {
                divMonth = 12;
            }
            retStr[0] = parseFullDate(curYear + "-" + divMonth + "-" + 1);
            retStr[1] = parseFullDate(curYear + "-" + divMonth + "-" + getLastDay(String.valueOf(curYear), String.valueOf(divMonth)));
            return retStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 取得当前年份（4位数字表示）
     *
     * @return String
     */

    public static String getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    /**
     * 取得当前是全年的第几周
     *
     * @return int
     */
    public static int getCurrentWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置一周从星期一开始
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 根据年份和某周取得某周的最后一天（星期日），若该周是该年的最后一周，返回yyyy-12-31
     *
     * @param yearStr 年份 格式是yyyy
     * @param weekStr 第几周
     * @return String 格式:yyyy-MM-dd
     */
    public static String getLastDayByWeek(String yearStr, String weekStr) {
        int year = Integer.parseInt(yearStr);
        int week = Integer.parseInt(weekStr);
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置一周从星期一开始
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 假设时间是这周的星期天
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        if (month == 1 && week > 6) {
            return parseFullDate(year + "-12-31");
        }
        return parseFullDate(year + "-" + month + "-" + day);
    }

    /**
     * 取得当月的最后一天
     */
    public static String getLastDayInCurMonth() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    /**
     * 取得某年某月的最后一天
     *
     * @param year
     * @param month
     * @return
     */
    public static String getLastDay(String year, String month) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        cal.set(Calendar.DATE, 1);
        return String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    /**
     * 取得某年2月的最后一天
     */
    public static String getLastDayForFeb(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        return String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    }

    /**
     * 根据年份和某周取得某周的最后一天（星期日），若该周是该年的第一周，返回yyyy-1-1
     *
     * @param yearStr 年份 格式是yyyy
     * @param weekStr 第几周
     * @return String 格式:yyyy-MM-dd
     */
    public static String getFirstDayByWeek(String yearStr, String weekStr) {
        int year = Integer.parseInt(yearStr);
        int week = Integer.parseInt(weekStr);
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY); // 设置一周从星期一开始
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 假设这周的星期一
        int day = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH) + 1;
        if (month != 1 && week == 1) {
            return parseFullDate(year + "-1-1");
        }
        return parseFullDate(year + "-" + month + "-" + day);
    }

    public static String parseFullDate(String str) {
        String[] strArr = str.split("-");
        if (strArr[1].length() == 1) {
            strArr[1] = "0" + strArr[1];
        }
        if (strArr[2].length() == 1) {
            strArr[2] = "0" + strArr[2];
        }
        return strArr[0] + "-" + strArr[1] + "-" + strArr[2];
    }

    /**
     * 根据日期（yyyy-MM-dd)取得日期是该年的第几周
     *
     * @param year  yyyy
     * @param month mm
     * @param day   dd
     * @return String
     */
    public static String getWeekByDate(String year, String month, String day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        cal.set(Calendar.DATE, Integer.parseInt(day));
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
    }

    /**
     * 将日期转换成 yyyy-MM-dd 格式
     *
     * @param date Date类
     * @return String
     */
    public static String convertToYYYYMMDD(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 取得当前年月 返回的格式是yyyy-MM
     *
     * @return String
     */
    public static String getCurYearMonth() {
        return getCurrentYear() + "-" + getCurMonth();
    }

    public static String convertToYYMMDDHHssSNoSep(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat timeStampFormatNoSep = new SimpleDateFormat("yyMMddHHmmssS");
        return timeStampFormatNoSep.format(date);
    }

    public static String convertToYYMMDDNoSep(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat yyMMddFormat = new SimpleDateFormat("yyMMdd");

        return yyMMddFormat.format(date);
    }

    /**
     * 转换日期格式 返回的格式是yyyy-MM-dd HH:mm:ss
     *
     * @param date Date类型
     * @return String
     */

    public static String convertToYYYYMMDDHHMMSS(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormat.format(date);
    }

    /**
     * 转换日期格式 返回的格式是yyyy-MM-dd HH:mm
     *
     * @param date Date类型
     * @return String
     */

    public static String convertToYYYYMMDDHHMM(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat hhmmFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return hhmmFormat.format(date);
    }

    /**
     * 转换日期格式 返回的格式是yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param date
     * @return
     */
    public static String convertToYYYYMMDDHHMMSSSSS(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return timeStampFormat.format(date);
    }

    /**
     * 将yyyy-MM-dd格式的字符串转换为Date对象
     *
     * @param dStr
     * @return
     * @throws Exception
     */
    public static Date string2Date(String dStr) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(dStr);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的字符串转换为Date对象
     *
     * @param dStr
     * @return
     * @throws Exception
     */
    public static Date string2Time(String dStr) throws Exception {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timeFormat.parse(dStr);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss.SSS格式的字符串转换为Date对象
     *
     * @param dStr
     * @return
     * @throws Exception
     */
    public static Date string2Timestamp(String dStr) throws Exception {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return timeStampFormat.parse(dStr);
    }

    /**
     * 返回strDate2和strDate1相差的天数，若strDate1 比strDate2 大则返回负数,相等返回0
     *
     * @param strDate1 yyyy-MM-dd
     * @param strDate2 yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public static int compareDate(String strDate1, String strDate2) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = dateFormat.parse(strDate1);
        Date date2 = dateFormat.parse(strDate2);
        long rel = date2.getTime() - date1.getTime();
        return (int) (rel / (1000 * 60 * 60 * 24));
    }

    /**
     * 返回strDate2和strDate1相差的天数，若strDate1 比strDate2 大则返回负数,相等返回0
     *
     * @param strDate1 yyyy-MM-dd
     * @param strDate2 yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public static int compareDate(Date strDate1, Date strDate2) throws Exception {
        Date date1 = strDate1;
        Date date2 = strDate2;
        long rel = date2.getTime() - date1.getTime();
        return (int) (rel / (1000 * 60 * 60 * 24));
    }

    /**
     * 返回strTime2和strTime1相差的小时数，若strTime1 比strTime2 大则返回负数
     *
     * @param strTime1 yyyy-MM-dd HH:mm:ss
     * @param strTime2 yyyy-MM-dd HH:mm:ss
     * @return
     * @throws Exception
     */
    public static long compareHour(String strTime1, String strTime2) throws Exception {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = timeFormat.parse(strTime1);
        Date date2 = timeFormat.parse(strTime2);
        long rel = date2.getTime() - date1.getTime();
        return rel / (1000 * 60 * 60);
    }

    /**
     * 返回strTime2和strTime1相差的分钟数，若strTime1 比strTime2 大则返回负数
     *
     * @param strTime1 yyyy-MM-dd HH:mm:ss
     * @param strTime2 yyyy-MM-dd HH:mm:ss
     * @return
     * @throws Exception
     */
    public static long compareMinute(String strTime1, String strTime2) throws Exception {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = timeFormat.parse(strTime1);
        Date date2 = timeFormat.parse(strTime2);
        long rel = date2.getTime() - date1.getTime();
        return rel / (1000 * 60);
    }

    public static long compareMinute(Date date1, Date date2) throws Exception {
        long rel = date2.getTime() - date1.getTime();
        return rel / (1000 * 60);
    }

    /**
     * 判断yyyy-MM-dd是否是有效的日期
     *
     * @param date
     * @return
     */
    public static boolean isInValidDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return false;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 判断yyyy-MM-dd HH:mm:ss是否是有效的日期时间格式
     *
     * @param date
     * @return
     */
    public static boolean isInValidDateTime(String date) {
        if (TextUtils.isEmpty(date)) {
            return false;
        }
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timeFormat.setLenient(false);
            timeFormat.parse(date);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 加时间后得到日期
     */
    public static Date addTime(Date date, long time) throws Exception {
        if (date == null) {
            return new Date();
        }
        Date newDate = new Date(date.getTime() + time);
        return newDate;
    }

    /**
     * 减时间后得到日期
     */
    public static Date subtructTime(Date date, long time) throws Exception {
        if (date == null) {
            return new Date();
        }
        Date newDate = new Date(date.getTime() - time);
        return newDate;
    }

    public static void main(String[] args) throws Exception {
        try {
            Date d = CalendarUtil.subtructTime(new Date(), 2 * 1 * 24 * 60 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据日期取得星期几的序号 星期1：2 星期日：1
     *
     * @param strDate yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public static int getWeekDay(String strDate) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(CalendarUtil.string2Date(strDate));
        int iRet = cal.get(Calendar.DAY_OF_WEEK);

        return iRet;
    }

    /**
     * 判断日期是否是周末
     *
     * @param strDate yyyy-MM-dd
     * @return
     * @throws Exception
     */
    public static boolean isWeekend(String strDate) throws Exception {
        int day = getWeekDay(strDate);
        return day == 7 || day == 1;
    }

    public static String[] splitDate(Date date) throws Exception {
        String[] retArr = new String[6];
        String strDate = convertToYYYYMMDDHHMMSS(date);
        String[] strArr1 = strDate.split(" ");
        String[] strArr2 = strArr1[0].split("-");
        String[] strArr3 = strArr1[1].split(":");
        retArr[0] = strArr2[0];
        retArr[1] = strArr2[1];
        retArr[2] = strArr2[2];

        retArr[3] = strArr3[0];
        retArr[4] = strArr3[1];
        retArr[5] = strArr3[2];

        return retArr;
    }
}
