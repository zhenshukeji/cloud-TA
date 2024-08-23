package com.zhenshu.common.enums.online;

/**
 * @program: jianxue
 * @description: 课程类型枚举
 * @author: tyh
 * @create: 2021-02-24 15:13
 **/
public enum LiveCourseTypeEnum {
    /**
     * 课程类型枚举
     * 0 线下课程 1 分销课程 2 团购课程 3 录播课程 4 直播课程
     */
    OFFLINE_COURSE(0, "线下课程"),
    DIST_COURSE(1, "分销课程"),
    GROUP_COURSE(2, "团购课程"),
    RECORD_COURSE(3, "录播课程"),
    LIVE_COURSE(4, "直播课程"),
    ;

    private int code;
    private String info;

    LiveCourseTypeEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }
}
