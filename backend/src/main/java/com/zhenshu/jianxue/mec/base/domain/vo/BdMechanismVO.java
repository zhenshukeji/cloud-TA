package com.zhenshu.jianxue.mec.base.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("机构信息主体")
public class BdMechanismVO extends InputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 机构名称
     */
    @ApiModelProperty(value = "机构名称", required = true)
    private String name;

    /**
     * 常用联系电话
     */
    @ApiModelProperty("常用联系电话")
    private String phoneNum;

    /**
     * 营业执照编号
     */
    @ApiModelProperty("营业执照编号")
    private String licenseCode;

    /**
     * 营业执照图片
     */
    @ApiModelProperty("营业执照图片")
    private String license;

    /**
     * 办学许可证编号
     */
    @ApiModelProperty("办学许可证编号")
    private String permitCode;

    /**
     * 办学许可证
     */
    @ApiModelProperty("办学许可证")
    private String permit;

    /**
     * logo，存储的为图片地址
     */
    @ApiModelProperty("logo，存储的为图片地址")
    private String logo;

    /**
     * 机构的详细地址
     */
    @ApiModelProperty("机构的详细地址")
    private String address;

    /**
     * 省份
     */
    @ApiModelProperty("省份")
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty("城市")
    private String city;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date creationTime;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    private Long creator;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("修改时间")
    private Date modifiedTime;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    private Long modifier;

    /**
     * 是否删除 0=未删除 1=删除
     */
    @ApiModelProperty("是否删除 0=未删除 1=删除")
    private Integer isDelete;

    /**
     * 封面图片
     */
    @ApiModelProperty("封面图片")
    private String mainImg;

    /**
     * 机构负责人名称
     */
    @ApiModelProperty("机构负责人名称")
    private String chargeName;

    /**
     * 状态 0待提交 1待审核 2审核通过 3审核未通过
     */
    @ApiModelProperty("状态 0待提交 1待审核 2审核通过 3审核未通过")
    private Integer status;

    /**
     * 拒绝原因
     */
    @ApiModelProperty("拒绝原因")
    private String refuseReason;

    /**
     * 审核时间
     */
    @ApiModelProperty("审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditedTime;

    /**
     * 邀请码
     */
    @ApiModelProperty("邀请码")
    private String inviteCode;

    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }

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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Long getCreator() {
        return creator;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifier(Long modifier) {
        this.modifier = modifier;
    }

    public Long getModifier() {
        return modifier;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
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
                .append("address", getAddress())
                .append("province", getProvince())
                .append("city", getCity())
                .append("creationTime", getCreationTime())
                .append("creator", getCreator())
                .append("modifiedTime", getModifiedTime())
                .append("modifier", getModifier())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
