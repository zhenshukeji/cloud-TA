package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2021/1/12 12:07
 * @desc 消息模板类型
 */
public enum TemplateMsgEnum {

    /**
     * 类型 0=续费预警 1=考勤通知 2=上课提醒
     */
    RENEWAL_WARNING(0, "续费预警"),
    ROLL_CALL(1, "考勤通知"),
    CLASS_REMINDER( 2, "上课提醒");

    private int code;
    private String info;

    TemplateMsgEnum(int code, String info) {
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
