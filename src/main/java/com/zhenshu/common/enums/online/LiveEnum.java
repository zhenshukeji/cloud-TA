package com.zhenshu.common.enums.online;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/9 16:06
 * @desc 直播枚举类
 */
public enum LiveEnum {

    /**
     * 线上课程枚举
     */
    NOT_LIVE(102, "未直播"),
    LIVING(101, "正在直播"),
    END_OF_LIVE(103, "直播已结束"),
    OPEN_GOODS(0, "开启商品货架"),
    CLOSE_GOODS(1, "关闭商品货架"),
    DIFFER(30, "相差时间"),
    // 0免费公开课1收费课
    FREE_PUBLIC_CLASS(0, "免费公开课"),
    TOLL_LESSON(1, "收费课")
    ;

    private final int code;
    private final String info;

    LiveEnum(int code, String info) {
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

