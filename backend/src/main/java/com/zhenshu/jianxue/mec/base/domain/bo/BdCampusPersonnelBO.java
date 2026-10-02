package com.zhenshu.jianxue.mec.base.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.aspectj.lang.annotation.Excel;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 校区人员对象 bd_campus_personnel
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("校区人员主体")
public class BdCampusPersonnelBO extends OutputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
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
    @Excel(name = "校区名称")
    @ApiModelProperty("校区名称")
    private String campusName;

    /**
     * 类型  0校区负责人 1校区人员
     */
    @Excel(name = "类型  0校区负责人 1校区人员")
    @ApiModelProperty("类型  0校区负责人 1校区人员")
    private Integer type;

    /**
     * 姓名
     */
    @Excel(name = "姓名")
    @ApiModelProperty("姓名")
    private String name;

    /**
     * 登录账号（之前是手机号，但是因为客户觉得手机号不好迁移，也不好随便新增对应的登录账号，为了使得账号可以继承，所以从手机号改成登录账号。不再校验只是手机号）
     */
    @ApiModelProperty("登录账号")
    @Excel(name = "类型  0校区负责人 1校区人员")
    private String phone;

    /**
     * 手机联系号码
     */
    private String contactNumber;

    /**
     * 入职时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入职时间", width = 30, dateFormat = "yyyy-MM-dd")
    @ApiModelProperty("入职时间")
    private Date joinTime;

    /**
     * 角色列表
     */
    @Excel(name = "角色id列表")
    @ApiModelProperty("角色id列表")
    private List<Long> roleIds;

    @ApiModelProperty("角色名称列表")
    private String roleNames;

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
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
        return "BdCampusPersonnelBO{" +
                "id=" + id +
                ", mecId=" + mecId +
                ", campusId=" + campusId +
                ", campusName='" + campusName + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", joinTime=" + joinTime +
                '}';
    }
}
