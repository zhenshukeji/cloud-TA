package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/24 15:47
 * @desc 消课表枚举
 */
public enum CallRecordEnum {
    /**
     * 到课状态
     */
    TO_CLASS(0, "到课"),
    LATE_STATE(1, "迟到"),
    LEAVE_STATE(2, "请假"),
    NOT_ARRIVE(3, "未到"),
    ADVANCE_STATE(4, "提前请假"),
    CONSUME_TIME_INITIAL(0, "消耗课次 初始值"),
    CONSUME_TIME(1, "消耗课时");

    private int code;
    private String info;

    CallRecordEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public static String getNameByType(int code){
        CallRecordEnum[] values = CallRecordEnum.values();
        for (CallRecordEnum value : values){
            if (code == value.getCode()){
                return value.getInfo();
            }
        }
        return "";
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
