package com.zhenshu.common.enums.account;

/**
 * @author jing
 * @version 1.0
 * @desc 平台账户流水
 * @date 2020/11/24 0024 19:14
 **/
public enum PlatFormTurnoverEnum {

    // 1=校区充值,2=学员充值,3=分销购买,4=团购购买,5=直播录播权限购买,6=会员权限购买,7=团购冻结金额入账,8=分销冻结金额入账
    CAMPUS_RECHARGE(1, "校区充值"),
    STUDENT_RECHARGE(2, "学员充值"),
    DISTRIBUTION(3, "分销购买"),
    GROUP(4, "团购购买"),
    LIVE_RECORD_PURCHASE(5, "直播录播权限购买"),
    MEMBER_PURCHASE(6, "会员权限购买"),
    GROUP_FROZEN_AMOUNT(7,"团购冻结金额入账"),
    DISTRIBUTION_FROZEN_AMOUNT(8,"分销冻结金额入账"),

    // 收支类型 0收入1支出
    INCOME(0, "收入"),
    EXPENSES(1, "支出"),

    // 币类型 0鉴学点1鉴学点2人民币
    JX_POINT(1, "鉴学点"),
    RMB(2, "人民币");

    private final int code;
    private final String info;

    PlatFormTurnoverEnum(int code, String info) {
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
