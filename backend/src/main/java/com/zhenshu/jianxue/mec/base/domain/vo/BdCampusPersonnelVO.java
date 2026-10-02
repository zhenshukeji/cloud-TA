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
 * 校区人员对象 bd_campus_personnel
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("校区人员主体")
public class BdCampusPersonnelVO extends InputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 机构id
     */
    @ApiModelProperty("机构id")
    private Long mecId;

    /**
     * 校区id
     */
    @ApiModelProperty("校区id")
    private Long campusId;

    /**
     * 校区名称
     */
    @ApiModelProperty("校区id")
    private String campusName;

    /**
     * 类型  0校区负责人 1校区人员
     */
    @ApiModelProperty("类型  0校区负责人 1校区人员")
    private Integer type;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String name;

    /**
     * 登录账号（之前是手机号，但是因为客户觉得手机号不好迁移，也不好随便新增对应的登录账号，为了使得账号可以继承，所以从手机号改成登录账号。不再校验只是手机号）
     */
    @ApiModelProperty("登录账号")
    private String phone;

    /**
     * 手机联系号码
     */
    private String contactNumber;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 入职时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("入职时间")
    private Date joinTime;

    /**
     * 角色对象
     */
    @ApiModelProperty("角色id列表")
    private Long[] roleIds;

    private Long roleId;

    private Integer isTeacher;

    public Integer getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(Integer isTeacher) {
        this.isTeacher = isTeacher;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public Long getMecId() {
        return mecId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public Long getCampusId() {
        return campusId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("type", getType())
                .append("name", getName())
                .append("phone", getPhone())
                .append("joinTime", getJoinTime())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
