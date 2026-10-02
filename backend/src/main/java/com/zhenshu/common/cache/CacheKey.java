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

    // OS014：补默认值。原先是无默认的 @Value 硬注入，任何一个 profile 漏配
    // 这两个 key 都会让**整个应用起不来**（实测 application-docker.yml 就漏了
    // cache_student_name）。社区版没有家长端，这两个前缀只用于 del 不存在的缓存。
    @Value("${spring.redis.cache_name:}")
    public String CACHE_NAME;

    @Value("${spring.redis.cache_student_name:}")
    public String CACHE_STUDENT_NAME;

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
