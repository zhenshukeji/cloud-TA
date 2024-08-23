package com.zhenshu.common.enums;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/22 15:56
 * @desc 周的枚举类
 */
public enum DayEnum {
    /**
     * 星期一
     */
    MONDAY(1, "一"),

    /**
     * 星期二
     */
    TUESDAY(2, "二"),

    /**
     * 星期三
     */
    WEDNESDAY(3, "三"),

    /**
     * 星期四
     */
    THURSDAY(4, "四"),

    /**
     * 星期五
     */
    FRIDAY(5, "五"),

    /**
     * 星期六
     */
    SATURDAY(6, "六"),

    /**
     * 星期日
     */
    SUNDAY(7, "日");
    private Integer weekday;

    private String day;

    DayEnum(Integer weekday, String day) {
        this.weekday = weekday;
        this.day = day;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public String getDay() {
        return day;
    }

    /**
     * 通过数字1-7获得中文
     * @param weekday 1-7
     * @return 一到七
     */
    public static String getDay(Integer weekday){
        DayEnum[] dayEnums = values();
        for (DayEnum dayEnum : dayEnums){
            if (dayEnum.getWeekday().equals(weekday)){
                return dayEnum.getDay();
            }
        }
        return "";
    }
}
