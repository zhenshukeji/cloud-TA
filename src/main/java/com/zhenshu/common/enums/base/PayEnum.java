package com.zhenshu.common.enums.base;

/**
 * @author tyh
 * @version 1.0
 * @desc 支付类型
 * @date 2021/1/28
 **/
public enum PayEnum {

    /**
     * 支付类型
     */
    PAY_WX(0, "微信支付"),
    ;

    private final Integer code;
    private final String info;

    PayEnum(Integer code, String info) {
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
