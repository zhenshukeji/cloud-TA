package com.zhenshu.common.enums.platform;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/12/7 14:23
 * @desc 平台人员
 */
public enum PlatPersonnelEnum {

    /**
     * 平台人员类型
     */
    MANAGER_PERSON(0, "平台管理员"),
    NORMAL_PERSON(1, "平台人员");

    private final int code;
    private final String info;

    PlatPersonnelEnum(int code, String info) {
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
