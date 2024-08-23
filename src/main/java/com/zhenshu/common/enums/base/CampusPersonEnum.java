package com.zhenshu.common.enums.base;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/27 11:32
 * @desc 校区人员信息枚举
 */
public enum CampusPersonEnum {


    //校区负责人 校区人员
    PRINCIPAL(0, "校区负责人"),
    ORDINARY_PEOPLE(1, "校区人员"),

    IS_NOT_TEACHER(0, "不是老师"),
    IS_TEACHER(1, "是老师");

    private final int code;
    private final String info;

    CampusPersonEnum(int code, String info) {
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
