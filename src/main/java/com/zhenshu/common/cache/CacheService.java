package com.zhenshu.common.cache;

import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.framework.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author jing
 * @version 1.0
 * @desc cache 业务类
 * @date 2020/6/11 0011 13:50
 **/
@Component
public class CacheService {

    @Autowired
    private RedisUtils cache;

    @Autowired
    CacheKey cacheKey;

    /**
     * 仅用于需要保存数据，但又不需要真实存在的时候
     */
    private static final String EXIST = "1";

    /**
     * 获取该手机号的发送缓存，判断一分钟内是否重复发送
     */
    public String getMobileSend(String phoneNum) {
        String key = cacheKey.getSendCodeCacheKey(phoneNum);
        return cache.getStr(key);
    }

    /**
     * 保存验证码发送记录
     */
    public void saveCheckCode(String phoneNum, String code) {
        String phoneKey = cacheKey.getSendCodeCacheKey(phoneNum);
        String codeKey = cacheKey.getCheckCodeKey(phoneNum);
        // 设置一分钟的有效期
        cache.set(phoneKey, EXIST, 60);
        // 设置五分钟的有效期
        cache.set(codeKey, code, 300);
    }

    /**
     * 获取验证码
     */
    public String getCheckCode(String phoneNum) {
        String key = cacheKey.getCheckCodeKey(phoneNum);
        return cache.getStr(key);
    }

    /**
     * 删除家长端首页内容推荐
     */
    public void delIndexRecommendCache() {
        String key = cacheKey.getIndexRecommendKey();
        cache.del(key);
    }

    /**
     * 删除家长端机构首页内容推荐
     */
    public void delMecRecommendCache() {
        // 当前登录的校区id
        Long campus = SecurityUtils.getLoginCampusId();
        String key = cacheKey.getMyMecRecommendKey(campus);
        cache.del(key);
    }

    /**
     * 删除家长端banner
     */
    public void delBanner() {
        Collection<String> keys = cache.keys(cacheKey.getBannerKey());
        if (keys == null || keys.isEmpty()){
            return;
        }
        cache.deleteObject(keys);
    }
}
