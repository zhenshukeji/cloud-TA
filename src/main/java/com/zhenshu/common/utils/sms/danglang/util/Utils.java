package com.zhenshu.common.utils.sms.danglang.util;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
	private static String defaultContentEncoding = "UTF-8";

	/**
     * 
    * @Title: writeToStream 
    * @Description: 向指定地址写入数据,并获取响应数据
     * 采用post方式请求 
    * @author heguan
    * @date 2016年5月17日 上午10:33:36 
    * @param @param addr 接口地址
    * @param @param info 输入字符串
    * @return String    返回类型 
    * @throws
     */
    public static String writeToStream(String addr, String info) {
        InputStream in = null;
        OutputStream outs = null;
        HttpURLConnection con = null;
        String result = "";
        try {
            URL url = new URL(addr);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            outs = con.getOutputStream();
            IOUtils.write(info, outs, defaultContentEncoding);
            in = con.getInputStream();
            result = IOUtils.toString(in, defaultContentEncoding);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e.getCause());
        } finally {
            IOUtils.closeQuietly(outs);
            IOUtils.closeQuietly(in);
            IOUtils.close(con);
        }
        return result;
    }
    
    /**生成sign签名
     * @param password 密钥
     * */
    public static String getSign(String password){
    	String sign = "";
    	
    	sign = EncryptTools.parseByte2HexStr(EncryptTools.encrypt(String.valueOf(System.currentTimeMillis()), password));
    	
    	return sign;
    }

}
