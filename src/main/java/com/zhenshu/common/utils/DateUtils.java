package com.zhenshu.common.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间工具类
 *
 * @author zhenshu
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static Calendar cal = Calendar.getInstance();

    public static SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String MM_DD = "MMdd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    public static Date getdayStart(Date date) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd");
        String startDate = day + " 00:00:00";
        try {
            return parseDate(startDate, YYYY_MM_DD_HH_MM_SS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getdayEnd(Date date) {
        String day = DateFormatUtils.format(date, "yyyy-MM-dd");
        String endDate = day + " 23:59:59";
        try {
            return parseDate(endDate, YYYY_MM_DD_HH_MM_SS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getNowYearDate() {
        return getYearDate(new Date());
    }

    public static int getYearDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    public static int getNowYearWeek() {
        return getYearWeek(new Date());
    }

    public static int getYearWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getNowYear() {
        return getYear(new Date());
    }

    public static String getNowYearDateStr() {
        int year = getNowYear();
        int date = getNowYearDate();
        return String.format("%d_%d", year, date);
    }

    public static String getNowYearWeekStr() {
        int year = getNowYear();
        int week = getNowYearWeek();
        return String.format("%d_%d", year, week);
    }

    /**
     * 获取当前是星期几
     *
     * @param date Date
     * @return int
     */
    public static int getDayOfWeek(Date date) {
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            return 7;
        }
        return day;
    }

    /**
     * 获取当前是星期几
     *
     * @param time String
     * @return int
     */
    public static int getDayOfWeek(String time) {
        Date date;
        try {
            date = sd.parse(time);
            return getDayOfWeek(date);
        } catch (ParseException e) {
            date = null;
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前是星期几 无需减一
     *
     * @return int
     */
    public static int getTodayOfWeek() {
        Date date = new Date();
        return getDayOfWeek(date);
    }

    /**
     * 获得今天的开始时间与结束时间
     *
     * @return 今天的开始时间与结束时间
     */
    public static Map<String, String> getDayTime(String date) {
        int capacity = 2;
        Map<String, String> resultMap = new HashMap<>(capacity);

        String startTimeDay = date.concat(" 00:00:00");
        resultMap.put("startTimeDay", startTimeDay);

        String endTimeDay = date.concat(" 23:59:59");
        resultMap.put("endTimeDay", endTimeDay);

        resultMap.put("today", date);
        return resultMap;
    }

    /**
     * 获取当前是星期几
     *
     * @param time String
     * @return int
     */
    public static int getWeekDay(String time) {
        Date date;
        try {
            date = sd.parse(time);
            int day = getDayOfWeek(date);
            if (day == 0) {
                return 7;
            }
            return day;
        } catch (ParseException e) {
            date = null;
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 系统前num天的日期
     *
     * @param num
     * @return
     */
    public static String getBefore(int num) {
        cal.add(Calendar.DAY_OF_MONTH, -num);

        String res = sd.format(cal.getTime());

        return res;
    }

    /**
     * 系统前num天的日期
     *
     * @param num
     * @return
     */
    public static String getAfter(int num) {
        cal.add(Calendar.DAY_OF_MONTH, +num);

        String res = sd.format(cal.getTime());

        return res;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(int past) {
        cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - past);
        Date today = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * 根据相差的周数，获取周的开始时间与结束时间
     *
     * @param num 那周 0表示当周 1表示+1周， -1表示减一周
     * @return Map<String, String>
     */
    public static Map<String, String> getWeekDayByDifferentNum(Integer num) {
        HashMap<String, String> time = new HashMap<>();
        //获取系统当前时间
        Date date = new Date();
        // 查询当前是星期几 星期天，星期一，星期二，星期三，星期四，星期五，星期六
        //0、1、2、3、4、5、6；
        int day = DateUtils.getDayOfWeek(date);
        // 本周开始时间
        Date nowWeekStartDay = new Date();
        switch (day) {
            //说明是星期天
            case 7:
                nowWeekStartDay = org.apache.commons.lang.time.DateUtils.addDays(date, -6);
                break;
            //星期一
            case 1:
                nowWeekStartDay = new Date();
                break;
            //星期二以后的
            default:
                nowWeekStartDay = org.apache.commons.lang.time.DateUtils.addDays(date, -day + 1);
        }
        // week 为0的情况，表示查当前周的
        if (num == 0) {
            // 本周结束时间
            Date nowWeekEndDay = org.apache.commons.lang.time.DateUtils.addDays(nowWeekStartDay, 6);
            time.put("before", DateUtils.dateTime(nowWeekStartDay) + " 00:00:00");
            time.put("after", DateUtils.dateTime(nowWeekEndDay) + " 23:59:59");
            time.put("monday", DateUtils.dateTime(nowWeekStartDay));
            time.put("sunday", DateUtils.dateTime(nowWeekEndDay));
        } else {
            // week 不为 0的情况 week 为负数表示上周 week 为正数表示下周
            int week = num;
            // 上周开始时间
            Date lastStartDay = org.apache.commons.lang.time.DateUtils.addWeeks(nowWeekStartDay, week);
            // 上周结束时间
            Date lastEndDay = org.apache.commons.lang.time.DateUtils.addDays(lastStartDay, 6);
            time.put("before", DateUtils.dateTime(lastStartDay) + " 00:00:00");
            time.put("after", DateUtils.dateTime(lastEndDay) + " 23:59:59");
            time.put("monday", DateUtils.dateTime(lastStartDay));
            time.put("sunday", DateUtils.dateTime(lastEndDay));
        }
        //计算这周星期一与星期天
        return getWeekDay(time);
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }


    public static double daysBetweenDetail(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        int daySecond = 1000 * 3600 * 24;
        int scale = 6;
        BigDecimal between_days = new BigDecimal(String.valueOf(time2 - time1)).divide(new BigDecimal(String.valueOf(daySecond)), scale, BigDecimal.ROUND_HALF_UP);

        return between_days.doubleValue();
    }

    /**
     * 获取某个日期范围内的每一天
     *
     * @param dBegin 开始时间
     * @param dEnd   结束时间
     */
    public static List<String> findDates(Date dBegin, Date dEnd) {
        //日期工具类准备
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //设置开始时间
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(dBegin);

        //设置结束时间
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(dEnd);

        //装返回的日期集合容器
        List<String> dateList = new ArrayList<>();

        // 每次循环给calBegin日期加一天，直到calBegin.getTime()时间等于dEnd
        while (dEnd.after(calBegin.getTime())) {
            dateList.add(format.format(calBegin.getTime()));
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dateList;
    }


    /**
     * 　　 *字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 获得周一到周日的日期
     *
     * @param date 具有原有的开始日期与结束日期
     * @return HashMap<String, String>
     */
    public static HashMap<String, String> getWeekDayByDay(Date date) {
        int day = DateUtils.getDayOfWeek(date);
        HashMap<String, String> time = new HashMap<>();
        Date monday;
        switch (day) {
            //说明是星期天
            case 7:
                monday = org.apache.commons.lang.time.DateUtils.addDays(date, -6);
                break;
            //星期一
            case 1:
                monday = new Date();
                break;
            //星期二以后的
            default:
                monday = org.apache.commons.lang.time.DateUtils.addDays(date, -day + 1);
        }
        return addDateFormat(time, monday);
    }

    @NotNull
    private static HashMap<String, String> addDateFormat(HashMap<String, String> time, Date monday) {
        String Tuesday = DateUtils.parseDateToStr(YYYY_MM_DD, org.apache.commons.lang.time.DateUtils.addDays(monday, 1));
        String Wednesday = DateUtils.parseDateToStr(YYYY_MM_DD, org.apache.commons.lang.time.DateUtils.addDays(monday, 2));
        String Thursday = DateUtils.parseDateToStr(YYYY_MM_DD, org.apache.commons.lang.time.DateUtils.addDays(monday, 3));
        String Friday = DateUtils.parseDateToStr(YYYY_MM_DD, org.apache.commons.lang.time.DateUtils.addDays(monday, 4));
        String Saturday = DateUtils.parseDateToStr(YYYY_MM_DD, org.apache.commons.lang.time.DateUtils.addDays(monday, 5));
        String Sunday = DateUtils.parseDateToStr(YYYY_MM_DD, org.apache.commons.lang.time.DateUtils.addDays(monday, 6));
        time.put("monday", DateUtils.parseDateToStr(YYYY_MM_DD, monday));
        time.put("tuesday", Tuesday);
        time.put("wednesday", Wednesday);
        time.put("thursday", Thursday);
        time.put("friday", Friday);
        time.put("saturday", Saturday);
        time.put("sunday", Sunday);
        return time;
    }

    /**
     * 获得周一到周日的日期
     *
     * @param time 具有原有的开始日期与结束日期
     * @return HashMap<String, String>
     */
    private static HashMap<String, String> getWeekDay(HashMap<String, String> time) {
        Date monday = DateUtils.parseDate(time.get("monday"));
        return addDateFormat(time, monday);
    }

    /**
     * 获取当前时间只前x天的时间列表
     *
     * @return 时间列表
     */
    public static List<String> getSubDayList(Integer day) {
        ArrayList<String> list = new ArrayList<>();
        Date date = new Date();
        list.add(DateFormatUtils.format(date, YYYY_MM_DD));
        for (int i = 1; i < day; i++) {
            String dateStr = DateUtils.parseDateToStr(YYYY_MM_DD, org.apache.commons.lang.time.DateUtils.addDays(date, -i));
            list.add(dateStr);
        }
        return list;
    }

    /**
     * 获取年月时间
     *
     * @return 时间列表
     */
    public static List<String> getYearMonthList() {
        ArrayList<String> list = new ArrayList<>();
        String year = DateFormatUtils.format(new Date(), YYYY);
        int twelve = 12;
        for (int i = 1; i <= twelve; i++) {
            String month = String.valueOf(i);
            if (i < 10){
                month = "0" + month;
            }
            String dateStr = String.format("%s-%s", year, month);
            list.add(dateStr);
        }
        return list;
    }

    /**
     * 获取精确到秒的时间戳
     *
     * @param date
     * @return
     */
    public static Long getSecondTimestampTwo(Date date) {
        if (null == date) {
            return 0L;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Long.valueOf(timestamp);
    }

    public static Date timeStampToDate(Long seconds) {
        if (seconds == null) {
            return null;
        }
        return new Date(seconds * 1000);
    }

    public static void main(String[] args) {
        Date date1 = DateUtils.parseDate("2021-01-18");
        Date date2 = DateUtils.parseDate("2021-01-24");
        System.out.println(findDates(date1, date2));
    }


    public static String getClassTime(String startInWeek, String endInWeek) {
        Date startDate = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", startInWeek);
        Date endDate = DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", endInWeek);
        String date = DateUtils.parseDateToStr("MM月dd日", startDate);
        String startTime = DateUtils.parseDateToStr("HH:mm", startDate);
        String endTime = DateUtils.parseDateToStr("HH:mm", endDate);
        return date + " " + startTime + "~" + endTime;
    }

    /**
     * 当前时间戳加年月日时分秒
     *
     * @return
     */
    public static String getTimeNO() {
        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdfTime.format(new Date());
    }

    /**
     * 获得当前时间是上午还是下午
     *
     * @return 上午还是下午
     */
    public static String getNowCopyWrite() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int a = Integer.parseInt(str);
        int zero = 0;
        int six = 6;
        int twelve = 12;
        int thirteen = 13;
        int eighteen = 18;
        int twentyFour = 24;
        if (a >= zero && a <= six) {
            return "凌晨好：";
        }
        if (a > six && a <= twelve) {
            return "上午好：";
        }
        if (a > twelve && a <= thirteen) {
            return "中午好：";
        }
        if (a > thirteen && a <= eighteen) {
            return "下午好：";
        }
        if (a > eighteen && a <= twentyFour) {
            return "晚上好：";
        }
        return "";
    }

}
