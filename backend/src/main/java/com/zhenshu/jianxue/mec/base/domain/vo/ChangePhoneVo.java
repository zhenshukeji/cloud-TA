package com.zhenshu.jianxue.mec.base.domain.vo;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/10 0010 12:09
 **/
public class ChangePhoneVo {

    private String phone;
    private String verifyCode;
    private String passWord;
    private String chargeName;

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}
