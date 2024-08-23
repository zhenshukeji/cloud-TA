package com.zhenshu.framework.web.domain;

import com.zhenshu.common.constant.HttpStatus;

public enum ResultCode {

    /* 成功状态码 */
    SUCCESS(HttpStatus.SUCCESS, "成功"),

    /* 失败状态码 */
    FAIL(HttpStatus.BAD_METHOD, "失败"),

    /**
     * 逻辑判断码
     */
    PURCHASE_LIVE(400100,"未购买直播录播功能，请先购买再使用"),
    MEMBER_PURCHASE(400200,"未购买会员不可使用该功能，请先购买再使用"),
    DELETE_TEACHER(400300,"删除老师"),
    SIGN(400400,"您今天已签到"),
    NOT_REPEAT_CALL(400500,"不可重复点名"),
    LAT_LNG_ERROR(400600,"未查询到该地址的经纬度，将无法在附近机构，附近课程，附近活动中出现，请确认是否需要修改详细地址。（注：需要有省市区）"),

    /* 系统500错误*/
    SYSTEM_ERROR(HttpStatus.ERROR, "访问人数过多，请稍后重试"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "签名验证失败"),
    TEST(500, "测试异常"),

    /* 参数错误：10001-19999 */
    PARAM_IS_INVALID(HttpStatus.BAD_REQUEST, "参数无效");

    private Integer code;

    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

}


