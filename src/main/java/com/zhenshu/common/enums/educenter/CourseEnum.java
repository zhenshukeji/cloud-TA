package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/24 15:59
 * @desc 课程信息
 */
public enum CourseEnum {
    /**
     * 课程信息枚举
     */
    EXPERIENCE(0, "体验课程"),
    ORDINARY(1, "常规课程"),
    OFFSHELF(0, "下架"),
    PUTSHELF(1, "上架");

    private int code;
    private String info;

    CourseEnum(int code, String info) {
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
