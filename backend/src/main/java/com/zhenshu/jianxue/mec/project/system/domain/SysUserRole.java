package com.zhenshu.jianxue.mec.project.system.domain;

/**
 * 用户和角色关联 sys_user_role
 *
 * @author zhenshu
 */
public class SysUserRole
{
    /** 用户ID */
    private Long userId;

    /** 角色ID */
    private Long roleId;

    /**
     * 用户类型 0是校区人员id  1是机构和校区的关联表id 2是平台人员
     */
    private Integer userType;

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "SysUserRole{" +
                "userId=" + userId +
                ", roleId=" + roleId +
                ", userType=" + userType +
                '}';
    }
}
