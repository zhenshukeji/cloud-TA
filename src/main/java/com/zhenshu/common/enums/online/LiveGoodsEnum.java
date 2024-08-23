package com.zhenshu.common.enums.online;

public enum LiveGoodsEnum {

    //auditStatus直播商品 审核状态 0：未审核，1：审核中，2:审核通过，3审核失败
    //审核状态
    UNREVIEWED(0,"未审核"),
    UNDER_REVIEW(1,"审核中"),
    EXAMINATION_PASSED(2,"审核通过"),
    AUDIT_FAILURE(3,"审核未通过"),

    ONE_PRICE(1,"一口价"),
    DISCOUNT_PRICE(3,"折扣价");

    private final int code;
    private final String info;

    LiveGoodsEnum(int code, String info) {
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
