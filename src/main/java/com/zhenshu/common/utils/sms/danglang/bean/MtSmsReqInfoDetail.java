package com.zhenshu.common.utils.sms.danglang.bean;

public class MtSmsReqInfoDetail {
	/**
	 * 可以是多个手机号码
	 */
	private String[] mobiles;
	/**
	 * 可以是多个手机号码,以","分隔
	 */
	private String mobile;
	/**
	 * 短信发送内容,需要注意的是，如果内容中包含签名，系统就不会补充签名
	 */
	private String smscontent;
	/**
	 * 短信定时时间，如果不填，则短信非定时短信
	 */
	private String schtime;
	/**
	 * 客户自定义smsid
	 */
	private String customSmsId;
	/**
	 * 扩展码
	 */
	private String extendedCode;
	/**
	 * 模板短信代码
	 */
	private String templeteCode;

	public String getSmscontent() {
		return smscontent;
	}

	public void setSmscontent(String smscontent) {
		this.smscontent = smscontent;
	}

	public String getSchtime() {
		return schtime;
	}

	public void setSchtime(String schtime) {
		this.schtime = schtime;
	}

	public String getCustomSmsId() {
		return customSmsId;
	}

	public void setCustomSmsId(String customSmsId) {
		this.customSmsId = customSmsId;
	}

	public String[] getMobiles() {
		return mobiles;
	}

	public void setMobiles(String[] mobiles) {
		this.mobiles = mobiles;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getExtendedCode() {
		return extendedCode;
	}

	public void setExtendedCode(String extendedCode) {
		this.extendedCode = extendedCode;
	}

	public String getTempleteCode() {
		return templeteCode;
	}

	public void setTempleteCode(String templeteCode) {
		this.templeteCode = templeteCode;
	}

}
