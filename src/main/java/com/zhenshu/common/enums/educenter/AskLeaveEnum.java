package com.zhenshu.common.enums.educenter;

/**
 * @author jing
 * @version 1.0
 * @desc 请假枚举
 * @date 2020/10/24 0024 16:47
 **/
public enum AskLeaveEnum {

    // 审核状态 0:未审核  2：未通过  1：已通过
    UN_REVIEW(0, "未审核"),
    NOT_PASS(2, "未通过"),
    PASS(1, "已通过");

    private int code;
    private String info;

    AskLeaveEnum(int code, String info) {
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
