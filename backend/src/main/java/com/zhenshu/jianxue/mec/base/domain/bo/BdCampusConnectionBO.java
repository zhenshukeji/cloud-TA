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
 * 机构人员和校区的关联 对象 bd_campus_connection
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("机构人员和校区的关联 主体")
public class BdCampusConnectionBO extends OutputEntity implements Serializable {
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
     * 机构人员id
     */
    @Excel(name = "机构人员id")
    @ApiModelProperty("机构人员id")
    private Long mecPersonnelId;
    /**
     * 校区id
     */

    @ApiModelProperty("校区id")
    private Long campusId;
    /**
     * 是否删除
     */

    @ApiModelProperty("是否删除")
    private Integer isDelete;

    /**
     * 校区名称
     */
    @ApiModelProperty("校区名称")
    private String campusName;

    /**
     * 角色名称权限列表，忘记用处了，但是留着吧
     */
    @ApiModelProperty("角色权限列表")
    private List<String> userRoleNames;

    /**
     * 机构人员名称
     */
    @ApiModelProperty("机构人员名称")
    private String mecName;

    /**
     * 机构登录账号
     */
    @ApiModelProperty("机构登录账号")
    private String mecPhone;

    /**
     * 机构人员联系电话
     */
    @ApiModelProperty("机构人员联系电话")
    private String mecContactNumber;

    /**
     * 用于展示角色名称
     */
    @ApiModelProperty("角色名称列表")
    private String roleNames;

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public String getMecName() {
        return mecName;
    }

    public void setMecName(String mecName) {
        this.mecName = mecName;
    }

    public String getMecPhone() {
        return mecPhone;
    }

    public void setMecPhone(String mecPhone) {
        this.mecPhone = mecPhone;
    }

    public List<String> getUserRoleNames() {
        return userRoleNames;
    }

    public void setUserRoleNames(List<String> userRoleNames) {
        this.userRoleNames = userRoleNames;
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

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getMecContactNumber() {
        return mecContactNumber;
    }

    public void setMecContactNumber(String mecContactNumber) {
        this.mecContactNumber = mecContactNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
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
