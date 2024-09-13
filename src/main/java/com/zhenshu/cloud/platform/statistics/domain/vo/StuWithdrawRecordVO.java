package com.zhenshu.cloud.platform.statistics.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jing
 * @version 1.0
 * @desc 用户提现入参
 * @date 2021/8/24 0024 17:00
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class StuWithdrawRecordVO extends InputEntity {

    /**
     * 学员姓名
     */
    private String stuName;

    /**
     * 学员手机号
     */
    private String stuPhone;

    /**
     * 提现结果 0失败 1成功 2待确认
     */
    private Integer result;

    /**
     * 微信商户订单号
     */
    private String wxOrderNo;

    /**
     * 提现开始时间 2021-08-24 18:54:00
     */
    private String startWithdrawTime;

    /**
     * 提现结束时间 2021-08-24 18:54:00
     */
    private String endWithdrawTime;

}
