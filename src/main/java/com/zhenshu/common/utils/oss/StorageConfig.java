package com.zhenshu.common.utils.oss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/6/29 0029 11:09
 **/
@Configuration
public class StorageConfig {

    @Value("${storage.type}")
    private String type;

    @Bean
    public Storage storage() {
        if (type != null) {
            if (type.equals("aliyun")) {
                return new AliyunOss();
            } else if (type.equals("tencent")) {
                return new TengcentOss();
            }
        }
        return null;
    }
}
