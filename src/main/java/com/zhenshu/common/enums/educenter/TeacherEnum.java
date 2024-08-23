package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/24 16:35
 * @desc 老师枚举类
 */
public enum TeacherEnum {
    /**
     * 老师信息枚举
     */
    ADD_CLASS_TAUGHT("add", "增加已授班级数"),
    REDUCE_CLASS_TAUGHT("reduce", "减少已授班级数"),
    RESIGN(1, "离职"),
    ON_JOB(0, "在职"),
    PERSON_MEC(0, "机构人员"),
    PERSON_CAMPUS(1, "校区人员"),
    INITIAL_TAUGHT_NUM(0, "初始已授班级数");

    private int code;
    private String info;
    private String msg;


    TeacherEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    TeacherEnum(String msg, String info) {
        this.msg = msg;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public String getMsg() {
        return msg;
    }
}
