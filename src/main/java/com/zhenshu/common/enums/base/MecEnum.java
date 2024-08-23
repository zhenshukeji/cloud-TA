package com.zhenshu.common.enums.base;

/**
 * @author jing
 * @version 1.0
 * @desc 机构状态枚举类
 * @date 2020/11/23 0023 15:41
 **/
public enum MecEnum {

    //类型  0待提交 1待审核 2审核通过 3审核未通过
    WAIT_SUBMIT(0, "待提交"),
    WAIT_REVIEW(1, "待审核"),
    PASSED(2, "审核通过"),
    FAILED(3, "审核未通过");

    private final int code;
    private final String info;

    MecEnum(int code, String info) {
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
