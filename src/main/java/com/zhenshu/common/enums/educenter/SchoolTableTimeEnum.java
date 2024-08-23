package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/24 16:17
 * @desc 课表枚举类
 */
public enum SchoolTableTimeEnum {

    /**
     * 课表枚举
     */
    CHANGE_TYPE(1, "id类型为调课id"),
    CLASSTIME_TYPE(0, "id类型为上课记录表id"),
    ADJUST_LESSON(2, "调课表中该条信息类型为调课"),
    CANCEL_LESSON(0, "调课表中该条信息类型为取消课次"),
    ADD_LESSON( 1, "调课表中该条信息类型为新增课次");

    private int code;
    private String info;

    SchoolTableTimeEnum(int code, String info) {
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
