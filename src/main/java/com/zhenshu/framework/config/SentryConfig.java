package com.zhenshu.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yuxi
 * @version 1.0
 * @date 2021/3/18 10:05
 * @desc sentry是否开启配置
 */
@Component
@ConfigurationProperties(prefix = "sentry")
@Data
public class SentryConfig {

    private boolean enable;
}
