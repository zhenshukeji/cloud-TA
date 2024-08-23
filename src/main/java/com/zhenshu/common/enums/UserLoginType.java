package com.zhenshu.common.enums;

/**
 * @author jing
 * @version 1.0
 * @desc 用户登录类型枚举
 * @date 2020/10/16 0016 10:39
 **/
public enum UserLoginType {

    // 机构管理员
    TYPE_MEC_MANAGER("0", "机构管理员"),
    // 机构人员
    TYPE_MEC_PERSONNEL("1", "机构人员"),
    // 校区管理员
    TYPE_CAMPUS_MANAGER("2", "校区管理员"),
    // 校区人员
    TYPE_CAMPUS_PERSONNEL("3", "校区人员"),
    // 平台管理员
    TYPE_PLATFORM_MANAGER("4", "平台管理员"),
    // 平台人员
    TYPE_PLATFORM_PERSONNEL("5", "平台人员"),

    // 平台管理员登录状态 0表示以平台管理员登录 1表示以机构管理员身份登录 2表示以校区管理员的身份登录
    LT_PLATFORM_PERSONNEL("0", "平台管理员"),
    LT_MEC_MANAGER("1", "机构管理员"),
    LT_CAMPUS_MANAGER("2", "校区管理员");

    private final String code;
    private final String info;

    UserLoginType(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
