package com.zhenshu.common.constant;

/**
 * @author jing
 * @version 1.0
 * @desc 支付配置
 * @date 2020/11/24 0024 14:29
 **/
public class PayConstant {

    /**
     * 支付详细信息
     */
    public final static String BODY = "充值鉴学点";

    /**
     * 外部支付类型  0微信支付
     */
    public final static int WX_PAY = 0;

    /**
     * 元转分或者分转元专用
     */
    public final static int HUNDRED = 100;

    /**
     * 支付时间格式化
     */
    public final static String DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * 外部支付表状态 1支付成功
     */
    public final static int SUCCESS = 1;

    /**
     * 保留小数位数
     */
    public final static int NEW_SCALE = 2;
}
