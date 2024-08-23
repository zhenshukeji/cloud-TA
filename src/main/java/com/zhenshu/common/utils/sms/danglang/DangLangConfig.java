package com.zhenshu.common.utils.sms.danglang;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/6/26 0026 11:37
 **/
@Configuration
@ConfigurationProperties("danglang")
public class DangLangConfig {

    private String appid;
    private String secret;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
