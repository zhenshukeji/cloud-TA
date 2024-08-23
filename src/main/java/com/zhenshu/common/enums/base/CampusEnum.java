package com.zhenshu.common.enums.base;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/26 10:02
 * @desc 校区枚举类
 */
public enum CampusEnum {

    /**
     * 是否会员 0=普通会员，1=黄金会员
     */
    IS_NOT_MEMBER (0, "普通会员"),
    IS_MEMBER(1, "黄金会员"),

    /**
     * 是否购买直播 0=未购买，1=已购买
     */
    IS_NOT_PURCHASE_LIVE(0,"未购买"),
    IS_PURCHASE_LIVE(1,"已购买"),

    ;

    private final Integer code;
    private final String info;

    CampusEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
