package com.zhenshu.common.enums.homeschool;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/27 17:26
 * @desc 家校通消息信息
 */
public enum InfoListEnum {

    // 信息类型0文本1图文2视频
    INFO_TYPE_TEXT(0, "文本类型"),
    INFO_TYPE_GRAPHIC(1, "图文类型"),
    INFO_TYPE_VIDEO(2, "视频类型"),

    //消息类型0学员发送1老师发送
    MSG_TYPE_STUDENT(0,"学生发送"),
    MSG_TYPE_TEACHER(1,"老师发送"),


    //是否收藏
    IS_NOT_COLLECT(0,"未收藏"),
    IS_COLLECT(1,"已收藏"),

    //是否已读
    IS_NOT_READ(0,"未读"),
    IS_READ(1,"已读");




    private final int code;
    private final String info;

    InfoListEnum(int code, String info) {
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
