package com.zhenshu.jianxue.mec.base.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 机构人员和校区的关联 对象 bd_campus_connection
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("机构人员和校区的关联 主体")
public class BdCampusConnectionVO extends InputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
        id
     */
    private Long id;

    /**
        机构id
     */
    @ApiModelProperty("机构id")
    private Long mecId;

    /**
        机构人员id
     */
    @ApiModelProperty("机构人员id")
    private Long mecPersonnelId;

    /**
        校区id
     */
    @ApiModelProperty("校区id")
    private Long campusId;

    /**
        是否删除
     */
    @ApiModelProperty("是否删除")
    private Integer isDelete;

    /**
     * 校区名称
     */
    @ApiModelProperty("校区名称")
    private String campusName;

    /**
     * 机构人员电话号码
     */
    @ApiModelProperty("机构人员电话号码")
    private String mecPhone;


    @ApiModelProperty("权限id")
    private Long[] roles;

    /**
     * 用于筛选角色
     */
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long[] getRoles() {
        return roles;
    }

    public void setRoles(Long[] roles) {
        this.roles = roles;
    }

    public String getMecPhone() {
        return mecPhone;
    }

    public void setMecPhone(String mecPhone) {
        this.mecPhone = mecPhone;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public Long getMecId() {
        return mecId;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public Long getMecPersonnelId() {
        return mecPersonnelId;
    }

    public void setMecPersonnelId(Long mecPersonnelId) {
        this.mecPersonnelId = mecPersonnelId;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public void setIsDelete(Integer isDelete)
    {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete()
    {
        return isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("mecPersonnelId", getMecPersonnelId())
                .append("campusId", getCampusId())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
