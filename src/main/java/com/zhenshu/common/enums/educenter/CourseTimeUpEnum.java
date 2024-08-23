package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/24 16:12
 * @desc 课时更新
 */
public enum CourseTimeUpEnum {

    /**
     * 课时更新枚举
     */
    ADD_OPR(0, "修改类型为新增"),
    REDUCE_OPR(1, "修改类型为减少"),
    HAND_WORK(1, "该条记录是工作人员手动增加课时"),
    PURCHASE_IN_CLASS(2, "该条记录是课时购买增加课时"),
    PURCHASE_REASON(2, "课程购买"),
    AUTH_COINS(3, "该条记录是鉴学点购买课时"),
    DISMISSAL(4, "该条记录是消课"),
    HAND_CHANGE_TOTAL(5, "手动修改总课次"),
    CHANGE_TO_LEAVE(6, "消课记录的到课状态修改为请假"),
    CHANGE_TO_OTHER_TYPE(7, "消课记录的到课状态修改为除请假以外的状态"),
    ;

    private final int code;
    private final String info;

    CourseTimeUpEnum(int code, String info) {
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
