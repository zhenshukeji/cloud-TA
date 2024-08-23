package com.zhenshu.common.enums.sale;

/**
 * @author jing
 * @version 1.0
 * @desc 活动状态
 * @date 2020/11/9 0009 14:25
 **/
public enum ActStatusEnum {

    //活动状态 0未发布 1未开始 2进行中 3已结束 4待审核 5审核不通过 6异常处理
    UN_PUBLISH(0, "未发布"),
    UN_START(1, "未开始"),
    PROCESSING(2, "进行中"),
    OVER(3, "已结束"),
    PENDING(4,"待审核"),
    FAIL(5,"审核不通过"),
    STATUS_INVALID(6,"市场活动状态错误");



    private final int code;
    private final String info;

    ActStatusEnum(int code, String info) {
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
