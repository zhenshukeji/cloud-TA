package com.zhenshu.jianxue.mec.base.domain.vo;

import java.io.Serializable;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/10 0010 15:11
 **/
public class ResetPwdVo implements Serializable {

    private Long id;

    private String pwd;

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
