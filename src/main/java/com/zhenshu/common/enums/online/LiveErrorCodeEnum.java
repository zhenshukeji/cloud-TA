package com.zhenshu.common.enums.online;

/**
 * @author jing
 * @version 1.0
 * @desc 直播商品报错信息枚举
 * @date 2021/3/17 0017 11:10
 **/
public enum  LiveErrorCodeEnum {


    /**
     * -1：系统错误
     * 1：未创建直播间
     * 1003：商品id不存在
     * 47001：入参格式不符合规范
     * 200002：入参错误
     * 300001：禁止创建/更新商品 或 禁止编辑&更新房间
     * 300002：名称长度不符合规则
     * 300006：图片上传失败（如：mediaID过期）
     * 300022：此房间号不存在
     * 300023：房间状态 拦截（当前房间状态不允许此操作）
     * 300024：商品不存在
     * 300025：商品审核未通过
     * 300026：房间商品数量已经满额
     * 300027：导入商品失败
     * 300029：主播昵称违规
     * 300030：主播微信号不合法
     * 300031：直播间封面图不合规
     * 300032：直播间分享图违规
     * 300033：添加商品超过直播间上限
     * 300034：主播微信昵称长度不符合要求
     * 300035：主播微信号不存在
     * 300036：主播微信号未实名认证
     * 300037：购物直播频道封面图不合规
     * 300038：未在小程序管理后台配置客服
     * 300039：主播副号微信号不合法
     * 300040：名称含有非限定字符（含有特殊字符）
     * 300041：创建者微信号不合法
     * 300042：推流中禁止编辑房间
     * 300043：每天只允许一场直播开启关注
     * 300044：商品没有讲解视频
     * 300045：讲解视频未生成
     * 300046：讲解视频生成失败
     * 300047：已有商品正在推送，请稍后再试
     * 300048：拉取商品列表失败
     * 300049：商品推送过程中不允许上下架
     * 300050：排序商品列表为空
     * 300051：解析JSON出错
     * 300052：已下架的商品无法推送
     * 300053：直播间未添加此商品
     * 500001：副号不合规
     * 500002：副号未实名
     * 500003：已经设置过副号了，不能重复设置
     * 500004：不能设置重复的副号
     * 500005：副号不能和主号重复
     * 600001：用户已被添加为小助手
     * 600002：找不到用户
     * 9410000: 直播间列表为空
     * 9410001: 获取房间失败
     * 9410002: 获取商品失败
     * 9410003: 获取回放失败
     */

    DIFFER_FROM_APP_CODE(200002, "直播开始时间大于当前时间30分钟且直播开始时间与结束时间间隔不得小于30分钟，直播时长不得大于24小时"),
    VERIFIED_CODE(300036, "实名认证"),
    ROOM_NOT_EXIST(300022, "此房间号不存在"),
    NOT_EDIT(300023, "房间状态拦截（当前房间状态不允许此操作）"),
    LIVE_NAME_CODE(300002, "直播间名字，最短3个汉字，最长17个汉字"),
    ERROR_PICTURE(300006, "图片上传失败，请重新上传"),
    NOT_ALLOW_NAME(300029, "主播昵称违规"),
    NOT_ALLOW_ROOM_NAME(300028, "房间名称违规"),
    ANCHOR_WRONGFUL(300030, "主播微信号不合法"),
    NOT_ALLOW_COVER(300031, "直播间封面图不合规"),
    NOT_ALLOW_SHARE(300032, "直播间分享图违规"),
    ANCHOR_NAME_ERROR(300034, "主播微信昵称长度不符合要求"),
    NOT_EXIST_WECHAT(300035, "主播微信号不存在"),
    NOT_VERIFIED(300036, "主播微信号未实名认证"),
    SUB_WRONGFUL(300039, "主播副号微信号不合法，请先进行实名认证"),
    HAVE_SPECIAL(300040, "名称含有非限定字符（含有特殊字符）"),
    CREATOR_WRONGFUL(300041, "创建者微信号不合法"),
    NOT_COMPLIANCE(500001, "副号不合规"),
    SUB_NOT_VERIFIED(500002, "副号未实名"),
    NOT_REPEAT(500005, "副号不能和主号重复"),


    GOOD_NOT_EXIST(300024, "商品不存在"),
    AUDIT_FAILED(300025, "商品审核未通过"),
    AMOUNT_FULL(300026, "房间商品数量已经满额"),
    IMPORT_FAIL(300027, "导入商品失败"),
    PUSHING_RETRY(300047, "已有商品正在推送，请稍后再试"),
    OVER_ONLINE(300033, "添加商品超过直播间上限"),
    PULL_GOODS_FAIL(300048, "拉取商品列表失败"),
    NO_VIDEO(300044, "商品没有讲解视频"),
    PUSHING_NO_SHELVES_ALLOWED(300049, "商品推送过程中不允许上下架"),
    SORT_GOODS(300050, "排序商品列表为空"),
    NOTADDED_GOODS(300053, "直播间未添加此商品"),
    HAS_BEEN_REMOVED(300052, "已下架的商品无法推送"),
    FAILED_TO_GET_ROOM(9410001, "获取房间失败"),
    OBTAIN_GOOD_FAIL(9410002, "获取商品失败"),

    APP_LIVE(101, "小程序返回状态直播中"),
    APP_NOT_START(102, "小程序返回状态未开始"),
    APP_FINISHED(103, "小程序返回状态已结束"),
    APP_NO_BROADCASTING(104, "小程序返回状态禁播"),
    APP_SUSPEND(105, "小程序返回状态暂停"),
    APP_ERROR(106, "小程序返回状态异常"),
    APP_OVERDUE(107, "小程序返回状态过期")
    ;

    private final int code;
    private final String info;

    LiveErrorCodeEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    /**
     * 获取描述
     *
     * @param code 状态码
     * @return 描述
     */
    public static String getDesc(String code) {
        LiveErrorCodeEnum[] checkInEnums = LiveErrorCodeEnum.values();
        for (LiveErrorCodeEnum checkInEnum : checkInEnums) {
            if (checkInEnum.getCode() == Integer.parseInt(code)) {
                return checkInEnum.getInfo();
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
