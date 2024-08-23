package com.zhenshu.common.enums.sale;

/**
 * @author jing
 * @version 1.0
 * @desc 分销枚举类
 * @date 2020/11/27 0027 17:05
 **/
public enum DistributionEnum {
    // 状态 0待支付 1=已完成（确认已报名）2=已退款 4=已支付
    WAIT_PAY(0, "待支付"),
    COMPLETE(1,"已完成"),
    PAID(4, "已支付"),
    REFUNDED(2,"已退款"),

    //课程类型 0录播课程1线下课程
    RECORD_COURSE(0, "录播课程"),
    OFFLINE_COURSE(1, "线下课程");

    private final int code;
    private final String info;

    DistributionEnum(int code, String info) {
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
