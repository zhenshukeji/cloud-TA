package com.zhenshu.jianxue.mec.project.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.framework.aspectj.lang.annotation.Excel;
import com.zhenshu.framework.aspectj.lang.annotation.Excel.ColumnType;
import com.zhenshu.framework.aspectj.lang.annotation.Excel.Type;
import com.zhenshu.framework.aspectj.lang.annotation.Excels;
import com.zhenshu.framework.web.domain.BaseEntity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author zhenshu
 */
public class SysUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Excel(name = "用户序号", cellType = ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;

    /**
     * 部门ID
     */
    @Excel(name = "部门编号", type = Type.IMPORT)
    private Long deptId;

    /**
     * 用户账号
     */
    @Excel(name = "登录名称")
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户名称")
    private String nickName;

    /**
     * 用户邮箱
     */
    @Excel(name = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @Excel(name = "手机号码")
    private String phonenumber;

    /**
     * 用户性别
     */
    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    @JsonIgnore
    private String password;

    /**
     * 盐加密
     */
    @JsonIgnore
    private String salt;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登陆IP
     */
    @Excel(name = "最后登陆IP", type = Type.EXPORT)
    private String loginIp;

    /**
     * 最后登陆时间
     */
    @Excel(name = "最后登陆时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    private Date loginDate;

    /**
     * 部门对象
     */
    @Excels({
            @Excel(name = "部门名称", targetAttr = "deptName", type = Type.EXPORT),
            @Excel(name = "部门负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    private SysDept dept;

    /**
     * 角色对象
     */
    private List<SysRole> roles;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 类型 0机构人员 1校区人员 2平台人员
     */
    private String type;

    /**
     * 关联id
     */
    private Long associationId;


    /**
     * 用于存放机构人员信息或者
     */
    private BdMecPersonnelBO mecPersonnel;


    /**
     * 用于存放校区人员信息
     */
    private BdCampusPersonnelBO campusPersonnel;

    /**
     * 用于存放 机构人员 或者 校区人员 关联的老师信息
     */
    private BdTeacherBO teacher;

    /**
     * 登录人员类型 0机构负责人 1机构人员 2校区管理员 3校区人员 4平台管理员
     */
    private String userType;

    /**
     * 机构管理员登录状态 0表示以机构管理员登录 1表示以校区管理员登录
     */
    private String mecMgrLoginType;

    /**
     * 登录范围 0表示以平台管理员登录 1表示以机构管理员身份登录 2表示以校区管理员的身份登录
     * 社区版（OS008）：无平台角色，该字段仅为兼容 PC 端既有读取而保留，写入方只剩「进入/返回校区」。
     */
    private String platformLoginType;

    /**
     * 当前登录的校区id
     */
    private Long campusId;

    /**
     * 当前登录的校区名称
     */
    private String campusName;

    /**
     * 当前登录的机构id
     */
    private Long mecId;

    /**
     * 当前登录的机构校区关联id
     */
    private Long campusConnectionId;

    public SysUser() {

    }

    public SysUser(Long userId) {
        this.userId = userId;
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    public boolean isAdmin() {
        return isAdmin(this.userId, this.type);
    }

    public static boolean isAdmin(Long userId, String type) {
        return userId != null && 1L == userId;
    }

    public Long getCampusConnectionId() {
        return campusConnectionId;
    }

    public void setCampusConnectionId(Long campusConnectionId) {
        this.campusConnectionId = campusConnectionId;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public Long getMecId() {
        return mecId;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public BdTeacherBO getTeacher() {
        return teacher;
    }

    public void setTeacher(BdTeacherBO teacher) {
        this.teacher = teacher;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public SysDept getDept() {
        return dept;
    }

    public void setDept(SysDept dept) {
        this.dept = dept;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds() {
        return postIds;
    }

    public void setPostIds(Long[] postIds) {
        this.postIds = postIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public BdMecPersonnelBO getMecPersonnel() {
        return mecPersonnel;
    }

    public void setMecPersonnel(BdMecPersonnelBO mecPersonnel) {
        this.mecPersonnel = mecPersonnel;
    }

    public BdCampusPersonnelBO getCampusPersonnel() {
        return campusPersonnel;
    }

    public void setCampusPersonnel(BdCampusPersonnelBO campusPersonnel) {
        this.campusPersonnel = campusPersonnel;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMecMgrLoginType() {
        return mecMgrLoginType;
    }

    public void setMecMgrLoginType(String mecMgrLoginType) {
        this.mecMgrLoginType = mecMgrLoginType;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public String getPlatformLoginType() {
        return platformLoginType;
    }

    public void setPlatformLoginType(String platformLoginType) {
        this.platformLoginType = platformLoginType;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "userId=" + userId +
                ", deptId=" + deptId +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", sex='" + sex + '\'' +
                ", avatar='" + avatar + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", loginIp='" + loginIp + '\'' +
                ", loginDate=" + loginDate +
                ", dept=" + dept +
                ", roles=" + roles +
                ", roleIds=" + Arrays.toString(roleIds) +
                ", postIds=" + Arrays.toString(postIds) +
                ", type='" + type + '\'' +
                ", associationId=" + associationId +
                ", mecPersonnel=" + mecPersonnel +
                ", campusPersonnel=" + campusPersonnel +
                ", userType='" + userType + '\'' +
                ", campusId=" + campusId +
                ", campusName='" + campusName + '\'' +
                '}';
    }
}
