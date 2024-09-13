package com.zhenshu.cloud.platform.statistics.domain.vo;

import java.math.BigDecimal;
import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * @desc 平台账户 对象 bd_platform_account
 *
 * @author yuxi
 * @date 2020-11-24
 */
@ApiModel("平台账户 主体")
public class BdPlatformAccountVO extends InputEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *  id
     */
    private Long id;
    /**
     *  总鉴学点
     */
    @ApiModelProperty("总鉴学点")
    private BigDecimal sumJianxuePoint;
    /**
     *  总人民币
     */
    @ApiModelProperty("总人民币")
    private BigDecimal sumRmb;
    /**
     *  版本号
     */
    @ApiModelProperty("版本号")
    private Integer version;
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
