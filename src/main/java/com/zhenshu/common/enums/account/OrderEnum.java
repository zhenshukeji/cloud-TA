package com.zhenshu.common.enums.account;

/**
 * @author jing
 * @version 1.0
 * @desc 订单枚举
 * @date 2020/11/10 0010 10:05
 **/
public enum OrderEnum {

    // 订单来源 1=退款，2=充值鉴学点，3=消费鉴学点，4=团购购买，5=直播购买，6=录播购买，7=分销购买,8=线下课程续费
    REFUND(1, "退款"),
    PURCHASE_JX_POINT(2, "充值鉴学点"),
    EXPENSES_JX_POINT(3, "消费鉴学点"),
    GROUP_PURCHASE(4, "团购购买"),
    LIVE_PURCHASE(5, "直播购买"),
    RECORD_PURCHASE(6, "录播购买"),
    DISTRIBUTION_PURCHASE(7, "分销购买"),
    OFFLINE_COURSE_PURCHASE(8, "线下课程续费"),
    MEMBER_PURCHASE(9, "会员购买"),
    LIVE_RECORD_PURCHASE(10, "直播录播购买"),

    // 业务类型 1=退款，2=充值鉴学点，3=消费鉴学点，4=团购购买，5=直播购买，6=录播购买，7=分销购买,8=线下课程续费,9=会员购买，10=直播录播购买
    B_REFUND(1, "退款"),
    B_PURCHASE_JX_POINT(2, "充值鉴学点"),
    B_EXPENSE_JX_POINT(3, "消费鉴学点"),
    B_GROUP_PURCHASE(4, "团购购买"),
    B_LIVE_PURCHASE(5, "直播购买"),
    B_RECORD_PURCHASE(6, "录播购买"),
    B_DISTRIBUTION_PURCHASE(7, "分销购买"),
    B_OFFLINE_COURSE_PURCHASE(8, "线下课程续费"),
    B_MEMBER_PURCHASE(9, "会员购买"),
    B_LIVE_RECORD_PURCHASE(10, "直播录播购买"),

    // 支付类型 1=鉴学点 ,2=人民币
    JX_POINT(1, "鉴学点"),
    RMB(2, "人民币"),

    // 订单状态 1、待支付 2、已完成 3、已取消 4、已退款 5、已支付未结算
    TO_BE_PAID(1,"待支付"),
    COMPLETE(2,"已完成"),
    CANCEL(3,"已取消"),

    // 账户类型· 0=机构支付 1=学员支付
    MEC_PAY(0,"机构支付"),
    STUDENT_PAY(1,"学员支付");

    private final int code;
    private final String info;

    OrderEnum(int code, String info) {
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
