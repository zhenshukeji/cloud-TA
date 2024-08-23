package com.zhenshu.common.enums.homeschool;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/29 0029 19:43
 **/
public enum BdGrowthLogEnum {
    // 来源0学员添加1教师添加2聊天记录
    STUDENT_ADD(0, "学员添加"),
    TEACHER_ADD(1, "教师添加"),
    CHAT_RECORD(2, "聊天记录");

    private final int code;
    private final String info;

    BdGrowthLogEnum(int code, String info) {
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
