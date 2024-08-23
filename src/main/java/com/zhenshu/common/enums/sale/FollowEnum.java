package com.zhenshu.common.enums.sale;

/**
 * 跟进状态
 */
public enum FollowEnum {
    //状态 0未开始 1已完成 2已取消 3已逾期
    UN_START(0, "未开始"),
    COMPLETE(1, "已完成"),
    CANCEL(2,"已取消"),
    OVERDUE(3,"已逾期");

    private final int code;
    private final String info;

    FollowEnum(int code, String info) {
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
