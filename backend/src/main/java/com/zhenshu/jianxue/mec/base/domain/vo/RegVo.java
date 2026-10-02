package com.zhenshu.jianxue.mec.base.domain.vo;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/6 0006 17:19
 **/
public class RegVo {

    /**
     * 机构名称
     */
    private String name;
    /**
     * 机构联系人电话
     */
    private String phone;
    /**
     * 验证码
     */
    private String capCode;
    /**
     * 密码
     */
    private String password;
    /**
     * 邀请码
     */
    private String inviteCode;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCapCode() {
        return capCode;
    }

    public void setCapCode(String capCode) {
        this.capCode = capCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}

