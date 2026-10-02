package com.zhenshu.jianxue.mec.base.domain.bo;

import com.zhenshu.framework.aspectj.lang.annotation.Excel;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;


/**
 * 机构人员 对象 bd_mec_personnel
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("机构人员 主体")
public class BdMecPersonnelBO extends OutputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 机构id
     */
    @ApiModelProperty("机构id")
    private Long mecId;

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
     * 是否删除
     */
    @ApiModelProperty("是否删除")
    private Integer isDelete;

    /**
     * 账号类型 0机构管理员 1机构人员
     */
    @Excel(name = "账号类型")
    @ApiModelProperty("账号类型 0机构管理员 1机构人员")
    private Integer type;


    /**
     * 与校区有关的表
     */
    @ApiModelProperty("校区列表")
    private List<BdCampusConnectionBO> campusConnectionList;

    public List<BdCampusConnectionBO> getCampusConnectionList() {
        return campusConnectionList;
    }

    public void setCampusConnectionList(List<BdCampusConnectionBO> campusConnectionList) {
        this.campusConnectionList = campusConnectionList;
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


    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
                .append("name", getName())
                .append("phone", getPhone())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
