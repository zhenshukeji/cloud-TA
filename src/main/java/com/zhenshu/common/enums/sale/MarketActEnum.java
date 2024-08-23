package com.zhenshu.common.enums.sale;

/**
 * 市场活动审核状态
 *
 * @author tyh
 * @desc 市场活动审核状态
 */
public enum MarketActEnum {
    //市场活动审核状态 0待审核 1通过 2不通过 3错误异常处理
    PENDING(1, "待审核"),
    PASS(2, "通过"),
    FAIL(3, "不通过"),
    STATUS_INVALID(4,"市场活动审核状态异常错误");

    private final int code;
    private final String info;

    MarketActEnum(int code, String info) {
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
