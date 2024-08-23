package com.zhenshu.common.utils.sms.aliyun;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.zhenshu.common.utils.jackson.JacksonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
 * 阿里云短信服务
 */
@Component
public class AliyunSmsSender {
    private final Log logger = LogFactory.getLog(AliyunSmsSender.class);

    @Autowired
    private AliyunSmsConfig smsConfig;


    public SmsResult sendTemplate(String phone, Map<String, String> data) {
        DefaultProfile profile = DefaultProfile.getProfile(smsConfig.getRegionId(), smsConfig.getAccessKeyId(), smsConfig.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", smsConfig.getRegionId());
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", smsConfig.getSign());
        request.putQueryParameter("TemplateCode", smsConfig.getTemplateId());
        String templateParam = JacksonUtil.toJson(data);
        request.putQueryParameter("TemplateParam", templateParam);

        SmsResult smsResult = new SmsResult();
        try {
            CommonResponse response = client.getCommonResponse(request);
            Map<String, String> map = JacksonUtil.toMap(response.getData());
            assert map != null;
            if (map.get("Code").equals("OK")) {
                smsResult.setSuccessful(true);
                smsResult.setResult(response);
            } else {
                smsResult.setSuccessful(false);
                logger.error(String.format("aliyun sms send errro !!! msg is %s", response.getData()));
            }
            return smsResult;
        } catch (ClientException e) {
            e.printStackTrace();
             logger.error(String.format("aliyun sms send exception !!! msg is %s", e.getMessage()));
            smsResult.setSuccessful(false);
            return smsResult;
        }
    }
}
