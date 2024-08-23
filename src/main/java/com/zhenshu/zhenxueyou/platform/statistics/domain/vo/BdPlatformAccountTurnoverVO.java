package com.zhenshu.zhenxueyou.platform.statistics.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @desc 平台账户流水 对象 bd_platform_account_turnover
 *
 * @author yuxi
 * @date 2020-11-23
 */
@ApiModel("平台账户流水 主体")
public class BdPlatformAccountTurnoverVO extends InputEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  id
     */
    private Long id;
    /**
     *  账户id
     */
    @ApiModelProperty("账户id")
    private Long accountId;
    /**
     *  内容
     */
    @ApiModelProperty("内容")
    private String content;
    /**
     *  收支类型 0收入1支出
     */
    @ApiModelProperty("收支类型 0收入1支出")
    private Integer incomeType;
    /**
     *  支付方名称
     */
    @ApiModelProperty("支付方名称")
    private String payerName;
    /**
     *  1、校区充值2、学员充值3、分销购买4、团购购买
     */
    @ApiModelProperty("1、校区充值2、学员充值3、分销购买4、团购购买")
    private Integer businessType;
    /**
     *  币类型 1鉴学点2人民币
     */
    @ApiModelProperty("币类型 1鉴学点2人民币")
    private Integer currencyType;
    /**
     *  币金额
     */
    @ApiModelProperty("币金额")
    private BigDecimal amount;
    /**
     *  订单号
     */
    @ApiModelProperty("订单号")
    private Long orderId;

    /**
     *  订单号
     */
    @ApiModelProperty("订单号")
    private String paymentId;

    /**
     *  是否删除
     */
    @ApiModelProperty("是否删除")
    private Integer isDelete;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setIncomeType(Integer incomeType) {
        this.incomeType = incomeType;
    }

    public Integer getIncomeType() {
        return incomeType;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setCurrencyType(Integer currencyType) {
        this.currencyType = currencyType;
    }

    public Integer getCurrencyType() {
        return currencyType;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("id", getId())
        .append("accountId", getAccountId())
        .append("content", getContent())
        .append("incomeType", getIncomeType())
        .append("payerName", getPayerName())
        .append("businessType", getBusinessType())
        .append("currencyType", getCurrencyType())
        .append("amount", getAmount())
        .append("orderId", getOrderId())
        .append("remark", getRemark())
        .append("createBy", getCreateBy())
        .append("createTime", getCreateTime())
        .append("updateBy", getUpdateBy())
        .append("updateTime", getUpdateTime())
        .append("isDelete", getIsDelete())
        .toString();
    }
}
