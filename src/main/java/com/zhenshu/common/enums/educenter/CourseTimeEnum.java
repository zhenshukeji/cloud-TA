package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/24 16:10
 * @desc 课时信息
 */
public enum CourseTimeEnum {

    /**
     * 课时信息
     */
    INITIAL(0, "初始值"),

    CHANGE_ADD(0, "课时增加"),
    CHANGE_REDUCE(1,"课时减少"),

    ONE_COURSE_TIME(1, "1课次"),
    ZERO_COURSE_TIME(0, "0课次")
    ;

    private final Integer code;
    private final String info;

    CourseTimeEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
