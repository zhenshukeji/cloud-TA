package com.zhenshu.common.enums.account;

/**
 * @author jing
 * @version 1.0
 * @desc 账户流水枚举
 * @date 2020/12/2 0002 14:34
 **/
public enum StuTurnoverEnum {

    // 收支类型 0收入1支出
    INCOME(0, "收入"),
    EXPENSES(1, "支出"),

    // 币类型 1鉴学点2人民币
    JX_POINT(1, "鉴学点"),
    RMB(2, "人民币"),


    /**
     * 学员流水业务类型 1、退款2、购买鉴学点 3、消费鉴学点  4、团购购买 5、直播购买6、录播购买7、分销购买 8、学员课次续费 9、团购退款 10、分销收入  11、提现 12、提现失败退回
     */
    REFUND(1, "退款"),
    PURCHASE_POINT(2, "充值鉴学点"),
    GROUP_PURCHASE(4, "团购购买"),
    LIVE_PURCHASE(5, "直播购买"),
    RECORD_PURCHASE(6, "录播购买"),
    DISTRIBUTION_PURCHASE(7, "分销购买"),
    COURSE_PURCHASE(8, "课次续费"),
    GROUP_REFUND(9, "团购退款"),
    DISTRIBUTION_INCOME(10, "分销收入"),
    WITHDRAW(11, "提现"),
    WITHDRAW_FAIL(12, "提现失败退回");

    private final int code;
    private final String info;

    StuTurnoverEnum(int code, String info) {
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
