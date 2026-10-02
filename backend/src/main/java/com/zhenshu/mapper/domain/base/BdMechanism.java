package com.zhenshu.mapper.domain.base;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;


/**
 * 机构信息对象 bd_mechanism
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_mechanism")
public class BdMechanism extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机构名称
     */
    private String name;

    /**
     * 常用联系电话
     */
    private String phoneNum;

    /**
     * 营业执照编号
     */
    private String licenseCode;

    /**
     * 营业执照图片
     */
    private String license;

    /**
     * 办学许可证编号
     */
    private String permitCode;

    /**
     * 办学许可证
     */
    private String permit;

    /**
     * logo，存储的为图片地址
     */
    private String logo;

    /**
     * 封面图片
     */
    private String mainImg;

    /**
     * 是否删除 0=未删除 1=删除
     */
    private Integer isDelete;

    /**
     * 机构负责人名称
     */
    private String chargeName;

    /**
     * 状态 0待提交 1待审核 2审核通过 3审核未通过
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String refuseReason;

    /**
     * 审核时间
     */
    private Date auditedTime;

    /**
     * 邀请码
     */
    private String inviteCode;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicense() {
        return license;
    }

    public void setPermitCode(String permitCode) {
        this.permitCode = permitCode;
    }

    public String getPermitCode() {
        return permitCode;
    }

    public void setPermit(String permit) {
        this.permit = permit;
    }

    public String getPermit() {
        return permit;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo() {
        return logo;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getAuditedTime() {
        return auditedTime;
    }

    public void setAuditedTime(Date auditedTime) {
        this.auditedTime = auditedTime;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("phoneNum", getPhoneNum())
                .append("licenseCode", getLicenseCode())
                .append("license", getLicense())
                .append("permitCode", getPermitCode())
                .append("permit", getPermit())
                .append("logo", getLogo())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
