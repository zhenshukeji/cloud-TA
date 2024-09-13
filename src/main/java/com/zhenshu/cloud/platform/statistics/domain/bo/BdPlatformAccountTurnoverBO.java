package com.zhenshu.cloud.platform.statistics.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.aspectj.lang.annotation.Excel;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author yuxi
 * @desc 平台账户流水 对象 bd_platform_account_turnover
 * @date 2020-11-23
 */
@ApiModel("平台账户流水 主体")
public class BdPlatformAccountTurnoverBO extends OutputEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 账户id
     */
    @ApiModelProperty("账户id")
    private Long accountId;

    /**
     * 内容
     */
    @ApiModelProperty("内容")
    @Excel(name = "项目")
    private String content;

    /**
     * 收支类型 0收入1支出
     */
    @ApiModelProperty("收支类型 0收入1支出")
    private Integer incomeType;

    /**
     * 支付方名称
     */
    @ApiModelProperty("支付方名称")
    @Excel(name = "支付方")
    private String payerName;

    /**
     * 1、校区充值2、学员充值3、分销购买4、团购购买
     */
    @ApiModelProperty("1、校区充值2、学员充值3、分销购买4、团购购买")
    @Excel(name = "支付类型", readConverterExp = "1=校区充值,2=学员充值,3=分销购买,4=团购购买,5=直播录播权限购买,6=会员权限购买")
    private Integer businessType;

    /**
     * 币金额
     */
    @ApiModelProperty("金额")
    @Excel(name = "金额")
    private BigDecimal amount;

    /**
     * 币类型 1鉴学点2人民币
     */
    @ApiModelProperty("币类型 1鉴学点2人民币")
    @Excel(name = "支付金额单位", readConverterExp = "1=鉴学点,2=人民币")
    private Integer currencyType;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private Long orderId;

    /**
     * 是否删除
     */
    @ApiModelProperty("是否删除")
    private Integer isDelete;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    @Excel(name = "关联订单号")
    private String paymentId;

    /**
     * 时间
     */
    @Excel(name = "时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
