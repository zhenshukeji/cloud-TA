package com.zhenshu.zhenxueyou.platform.statistics.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author yuxi
 * @desc 平台账户 对象 bd_platform_account
 * @date 2020-11-24
 */
@ApiModel("平台账户 主体")
public class BdPlatformAccountBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 总鉴学点
     */
    @ApiModelProperty("总鉴学点")
    private BigDecimal sumJianxuePoint;

    /**
     * 总人民币
     */
    @ApiModelProperty("总人民币")
    private BigDecimal sumRmb;

    /**
     * 版本号
     */
    @ApiModelProperty("版本号")
    private Integer version;

    /**
     * 是否删除
     */
    @ApiModelProperty("是否删除")
    private Integer isDelete;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("待结算金额")
    private BigDecimal toBeSettledAmount;

    public BigDecimal getToBeSettledAmount() {
        return toBeSettledAmount;
    }

    public void setToBeSettledAmount(BigDecimal toBeSettledAmount) {
        this.toBeSettledAmount = toBeSettledAmount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setSumJianxuePoint(BigDecimal sumJianxuePoint) {
        this.sumJianxuePoint = sumJianxuePoint;
    }

    public BigDecimal getSumJianxuePoint() {
        return sumJianxuePoint;
    }

    public void setSumRmb(BigDecimal sumRmb) {
        this.sumRmb = sumRmb;
    }

    public BigDecimal getSumRmb() {
        return sumRmb;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sumJianxuePoint", getSumJianxuePoint())
                .append("sumRmb", getSumRmb())
                .append("version", getVersion())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
