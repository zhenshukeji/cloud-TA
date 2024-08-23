package com.zhenshu.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/14 10:07
 * @desc
 */
@Component
@ConfigurationProperties(prefix = "baidu")
public class BaiduConfig {

    private String ak;

    private String  url;

    private String output;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }
}
