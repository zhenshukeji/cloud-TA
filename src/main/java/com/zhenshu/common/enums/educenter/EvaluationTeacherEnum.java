package com.zhenshu.common.enums.educenter;

/**
 * @author jing
 * @version 1.0
 * @desc 评价老师枚举
 * @date 2020/10/26 0026 11:46
 **/
public enum EvaluationTeacherEnum {
    /**
     * 学员评价老师类型
     */
    ANONYMOUS(0, "匿名"),
    REAL_NAME(1, "实名"),

    ;

    private final int code;
    private final String info;

    EvaluationTeacherEnum(int code, String info) {
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
