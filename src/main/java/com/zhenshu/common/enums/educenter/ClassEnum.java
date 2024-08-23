package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/24 16:04
 * @desc 班级信息
 */
public enum ClassEnum {
    /**
     * 班级枚举
     */
    EXPERIENCE(0, "体验班级"),
    ORDINARY(1, "常规班级"),
    NORMAL_STATUS(0, "班级状态为正常状态"),
    ABANDONED_STATUS(1, "班级状态为废弃状态"),
    INITIAL(0, "班级创建初始值");

    private int code;
    private String info;

    ClassEnum(int code, String info) {
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
