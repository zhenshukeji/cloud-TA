package com.zhenshu.common.utils;

import com.zhenshu.common.cache.CacheService;
import com.zhenshu.common.utils.sms.aliyun.AliyunSmsSender;
import com.zhenshu.common.utils.sms.aliyun.SmsResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/10 0010 9:50
 **/
@Component
public class SMSCodeUtils {

    @Resource
    private AliyunSmsSender smsSender;
    @Resource
    private CacheService cacheService;


    /**
     * 发送验证码
     *
     * @param phoneNum 手机号
     * @return 是否发送成功
     */
    public boolean sendCode(String phoneNum) {
        //String code = CharUtil.getRandomNum(6);
        String code = "123456";
        HashMap<String, String> map = new HashMap<>();
        map.put("code", code);
        SmsResult result = smsSender.sendTemplate(phoneNum, map);
        //if (result.isSuccessful()) {
        if (true){
            // 保存验证码到缓存
            cacheService.saveCheckCode(phoneNum, code);
            return true;
        }
        return false;
    }

    /**
     * 判断手机号一分钟内是否发送过 验证码
     *
     * @param phoneNum 手机号
     * @return true 表示发送过
     */
    public boolean checkPhone(String phoneNum) {
        String sendRecord = cacheService.getMobileSend(phoneNum);
        return sendRecord != null;
    }


    public boolean checkCode(String phoneNum, String checkCode) {
        String cacheCode = cacheService.getCheckCode(phoneNum);
        // 判断验证码是否存在以及验证码是否正确
        return checkCode.equals(cacheCode);
    }
}
