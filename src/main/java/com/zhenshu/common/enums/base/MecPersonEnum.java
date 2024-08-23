package com.zhenshu.common.enums.base;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/27 11:57
 * @desc 机构人员枚举信息
 */
public enum MecPersonEnum {

    //类型 0机构管理员 1机构人员
    PRINCIPAL (0, "机构管理员"),
    ORDINARY_PEOPLE(1, "机构人员");

    private final int code;
    private final String info;

    MecPersonEnum(int code, String info) {
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
