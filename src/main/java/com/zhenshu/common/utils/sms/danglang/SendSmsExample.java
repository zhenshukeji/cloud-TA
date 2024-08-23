package com.zhenshu.common.utils.sms.danglang;

import com.zhenshu.common.utils.sms.danglang.bean.MtSmsReqInfoDetail;
import com.zhenshu.common.utils.sms.danglang.util.EncryptTools;
import com.zhenshu.common.utils.sms.danglang.util.Utils;
import com.zhenshu.common.utils.sms.danglang.util.json.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class SendSmsExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String addr = "http://sdk.gzdlinfo.cn/dlcpInterface/sendsms";
		String appId = "GZDLT7QC72677617166";//AppID
		String pwd = "6H4H9";//密钥
		String sign = Utils.getSign(pwd);//生成签名
		
		List<MtSmsReqInfoDetail> mtSmsReqInfoDetailList = new ArrayList<MtSmsReqInfoDetail>();
		
		for (int i = 0; i < 1; i++){
			MtSmsReqInfoDetail mtSmsReqInfoDetail = new MtSmsReqInfoDetail();
			String mobile = "18682044979";
			mtSmsReqInfoDetail.setMobile(mobile);
			mtSmsReqInfoDetail.setCustomSmsId(String.valueOf(System.currentTimeMillis()));
			String content = "验证码：123456";
			mtSmsReqInfoDetail.setSmscontent(content);

			mtSmsReqInfoDetailList.add(mtSmsReqInfoDetail);
		}
		
		byte[] b = EncryptTools.encrypt(JsonUtil.getJsonArray(mtSmsReqInfoDetailList), pwd);//AES加密
		
		String smsData = EncryptTools.parseByte2HexStr(b);

		String info = "appId="+appId+"&sign="+sign+"&smsData="+smsData;
		System.out.println("info:" + info);
		String respones = Utils.writeToStream(addr,info);
		
		System.out.println("result:" + respones);
		
		/*//解析json
		if (respones != null && !respones.isEmpty()){
			Map<String, Class> map = new HashMap<String,Class>();
			map.put("smses", MtSmsResponseDetail.class);
			MtSmsResponse smsResponseBaseInfo = JsonUtil.Json2ObjMap(respones,MtSmsResponse.class,map);
		}*/
	}
}
