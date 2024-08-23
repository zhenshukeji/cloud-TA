package com.zhenshu.common.enums.homeschool;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/28 18:43
 * @desc 意见反馈枚举类
 */
public enum FeedbackEnum {

    //意见类型1文本2图片3图文
    TEXT(1, "文本"),
    PICTURE(2, "图片"),
    GRAPHIC(3,"图文"),

    //是否处理 0=未处理 1=处理
    PROCESSED(1,"处理"),
    UNTREATED(0,"未处理");


    private final int code;
    private final String info;

    FeedbackEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
