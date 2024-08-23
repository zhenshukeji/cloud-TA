package com.zhenshu.zhenxueyou.platform.statistics.domain.bo;

import com.zhenshu.framework.aspectj.lang.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 提现记录
 * </p>
 *
 * @author jing
 * @since 2021-08-13
 */
@Data
public class BdStuWithdrawRecordBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 学员id
     */
    private Long userId;

    /**
     * 学员姓名
     */
    @Excel(name = "学员姓名")
    private String stuName;

    /**
     * 学员手机号
     */
    @Excel(name = "学员手机号")
    private String stuPhone;

    /**
     * 提现金额
     */
    @Excel(name = "提现金额")
    private BigDecimal withdrawAmount;

    /**
     * 提现结果 0失败 1成功 2待确认
     */
    @Excel(name = "提现结果", readConverterExp="0=失败,1=成功,2=待确认")
    private Integer result;

    /**
     * 失败原因
     */
    @Excel(name = "微信返回失败原因")
    private String failReason;

    /**
     * 对外展示的失败原因
     */
    @Excel(name = "对外展示的失败原因")
    private String showFailReason;

    /**
     * 微信商户订单号
     */
    @Excel(name = "微信商户订单号")
    private String wxOrderNo;

    /**
     * 重试次数
     */
    private Integer numberOfRetries;

    /**
     * 提现时间
     */
    @Excel(name = "提现时间")
    private LocalDateTime withdrawTime;

    /**
     * 到账时间
     */
    @Excel(name = "到账时间")
    private LocalDateTime paymentTime;

}
