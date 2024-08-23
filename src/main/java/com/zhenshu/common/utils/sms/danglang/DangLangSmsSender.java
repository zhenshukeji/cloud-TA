package com.zhenshu.common.utils.sms.danglang;

import com.fasterxml.jackson.databind.JsonNode;
import com.zhenshu.common.utils.jackson.JacksonUtil;
import com.zhenshu.common.utils.sms.aliyun.SmsResult;
import com.zhenshu.common.utils.sms.danglang.bean.MtSmsReqInfoDetail;
import com.zhenshu.common.utils.sms.danglang.util.EncryptTools;
import com.zhenshu.common.utils.sms.danglang.util.Utils;
import com.zhenshu.common.utils.sms.danglang.util.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/6/26 0026 11:32
 **/
@Component
public class DangLangSmsSender {
    @Autowired
    private DangLangConfig dangLangConfig;

    public SmsResult sendTemplate(String mobile, Map<String, String> data) {
        //生成签名
        String sign = Utils.getSign(dangLangConfig.getSecret());
        List<MtSmsReqInfoDetail> mtSmsReqInfoDetailList = new ArrayList<MtSmsReqInfoDetail>();
        //  参数格式整理
        MtSmsReqInfoDetail mtSmsReqInfoDetail = new MtSmsReqInfoDetail();
        mtSmsReqInfoDetail.setMobile(mobile);
        mtSmsReqInfoDetail.setCustomSmsId(String.valueOf(System.currentTimeMillis()));

        String template = "尊敬的用户，您的验证码为：%s，5分钟后过期，请勿泄露于他人。";
        String content = String.format(template, data.get("code"));
        mtSmsReqInfoDetail.setSmscontent(content);
        mtSmsReqInfoDetailList.add(mtSmsReqInfoDetail);
        //AES加密
        byte[] encryptByte = EncryptTools.encrypt(JsonUtil.getJsonArray(mtSmsReqInfoDetailList), dangLangConfig.getSecret());
        assert encryptByte != null;
        // 十六进制转换
        String smsData = EncryptTools.parseByte2HexStr(encryptByte);
        // post 传参转换
        String info = String.format("appId=%s&sign=%s&smsData=%s", dangLangConfig.getAppid(), sign, smsData);
        //String info = "appId=" + dangLangConfig.getAppid() + "&sign=" + sign + "&smsData=" + smsData;

        String url = "http://sdk.gzdlinfo.cn/dlcpInterface/sendsms";
        String response = Utils.writeToStream(url, info);
        //解析json
        SmsResult smsResult = new SmsResult();
        if (response != null && !response.isEmpty()) {
            JsonNode result = JacksonUtil.toNode(response);
            // 判断是否发送成功
            if ("SUCCESS".equals(result.get("code").asText()) && result.get("total").asInt() == 1) {
                smsResult.setSuccessful(true);
                return smsResult;
            }
        }
        smsResult.setSuccessful(false);
        return smsResult;
    }

}
