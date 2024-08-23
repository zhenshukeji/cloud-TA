package com.zhenshu.common.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author jing
 * @version 1.0
 * @desc redis缓存key类
 * @date 2020/6/11 0011 13:50
 **/
@Component
public class CacheKey {

    @Value("${spring.redis.cache_name}")
    public String CACHE_NAME;

    @Value("${spring.redis.cache_student_name}")
    public String CACHE_STUDENT_NAME;

    /**
     * 手机号发送时间缓存
     */
    public String getSendCodeCacheKey(String phoneNum) {
        return CACHE_NAME + "_send_cache_" + phoneNum;
    }

    /**
     * 验证码缓存
     */
    public String getCheckCodeKey(String phoneNum) {
        return CACHE_NAME + "_check_code_" + phoneNum;
    }


    /**
     * 家长端内容推荐
     */
    public String getIndexRecommendKey() {
        return String.format("%s_index_recommend", CACHE_STUDENT_NAME);
    }

    /**
     * 家长端我的机构内容推荐
     */
    public String getMyMecRecommendKey(Long campusId) {
        return String.format("%s_campus_recommend_id:%s", CACHE_STUDENT_NAME, campusId);
    }


    /**
     * 家长端banner
     */
    public String getBannerKey() {
        return CACHE_STUDENT_NAME + "_banners";
    }
}
