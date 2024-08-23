package com.zhenshu.common.enums.platform;

/**
 * @author jing
 * @version 1.0
 * @desc 平台资讯
 * @date 2020/11/17 0017 11:25
 **/
public enum PlatformInformationEnum {
    /**
     * 资讯类型 0=机构端，1=家长端
     */
    MEC(0, "机构端"),
    STUDENT(1, "家长端"),
    ;

    private int code;
    private String info;

    PlatformInformationEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }
}
