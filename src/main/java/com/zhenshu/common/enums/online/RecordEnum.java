package com.zhenshu.common.enums.online;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/11 12:04
 * @desc 录播课程枚举类
 */
public enum RecordEnum {

    /**
     * 录播课程上下架状态
     */
    OFF_SHELF(1, "下架"),
    ON_SHELF(0, "上架"),;

    private int code;
    private String info;

    RecordEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }
}
