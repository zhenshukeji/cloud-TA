package com.zhenshu.common.utils;

import com.github.promeg.pinyinhelper.Pinyin;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/15 0015 11:29
 **/
public class PinYinUtils {

    /**
     * 文字转拼音
     *
     * @param src 文字
     * @return 拼音
     */
    public static String strToPinYin(String src) {
        char[] chars = src.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char aChar : chars) {
            // 判断是否为汉字字符
            if (Character.toString(aChar).matches("[\\u4E00-\\u9FA5]+")) {
                sb.append(Pinyin.toPinyin(aChar));
            } else
                sb.append(aChar);
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        String text = "这里是文字";
        String pinYin = strToPinYin(text);
        System.out.println(String.format("这里是拼音: %s", pinYin));
    }
}
