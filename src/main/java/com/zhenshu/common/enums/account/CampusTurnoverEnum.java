package com.zhenshu.common.enums.account;

/**
 * @author jing
 * @version 1.0
 * @desc 账户流水枚举
 * @date 2020/11/10 0010 10:05
 **/
public enum CampusTurnoverEnum {

    // 收支类型 0收入1支出
    INCOME(0, "收入"),
    EXPENSES(1, "支出"),

    // 流水类型  1=退款，2=充值鉴学点，3=消费鉴学点，4=团购购买，5=直播购买，6=录播购买，7=分销购买,8=线下课程续费,9=会员购买，10=直播录播购买,11=机构签到，12=提现
    SIGN(11,"机构签到"),
    WITHDRAWAL(12,"提现"),

    // 币类型 1鉴学点2人民币
    JX_POINT(1, "鉴学点"),
    RMB(2, "人民币");

    private final int code;
    private final String info;

    CampusTurnoverEnum(int code, String info) {
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
