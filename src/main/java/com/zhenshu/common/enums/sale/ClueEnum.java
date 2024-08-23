package com.zhenshu.common.enums.sale;

/**
 * @author jing
 * @version 1.0
 * @desc 线索枚举
 * @date 2020/11/10 0010 11:02
 **/
public enum ClueEnum {

    //学员来源 0市场活动  1机构官网 2线下地推 3门店到访  4转介绍 5其他
    MK_ACTIVITY(0, "市场活动"),
    WEBSITE(1, "机构官网"),
    OFFLINE_PUSH(2, "线下地推"),
    STORE_VISIT(3, "门店到访"),
    TRANSFER_INTRODUCTION(4, "转介绍"),
    OTHER(5, "其他"),

    //意向级别 0一星 1二星 2三星 3四星 4五星 5其他
    ONE_STAR(0,"一星"),
    TWO_STAR(1,"二星"),
    THREE_STAR(2,"三星"),
    FOUR_STAR(3,"四星"),
    FIVE_STAR(4,"五星"),
    OTHER_STAR(5,"其他");

    private final int code;
    private final String info;

    ClueEnum(int code, String info) {
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
