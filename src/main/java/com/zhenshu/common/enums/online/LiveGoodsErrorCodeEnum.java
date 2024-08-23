package com.zhenshu.common.enums.online;

/**
 * @author jing
 * @version 1.0
 * @desc 直播商品报错状态码类
 * @date 2021/3/17 0017 11:07
 **/
public enum LiveGoodsErrorCodeEnum {


    /**
     * -1：系统错误
     * 1003：商品id不存在
     * 47001：入参格式不符合规范
     * 200002：入参错误
     * 300001：禁止创建/更新商品（如：商品创建功能被封禁）
     * 300002：名称长度不符合规则
     * 300003：价格输入不合规（如：现价比原价大、传入价格非数字等）
     * 300004：商品名称存在违规违法内容
     * 300005：商品图片存在违规违法内容
     * 300006：图片上传失败（如：mediaID过期）
     * 300007：线上小程序版本不存在该链接
     * 300008：添加商品失败>
     * 300009：商品审核撤回失败
     * 300010：商品审核状态不对（如：商品审核中）
     * 300011：操作非法（API不允许操作非API创建的商品）
     * 300012：没有提审额度（每天500次提审额度）
     * 300013：提审失败
     * 300014：审核中，无法删除（非零代表失败）
     * 300015:商品不存在
     * 300017：商品未提审
     * 300018：商品图片尺寸过大
     * 300021：商品添加成功，审核失败
     */
    //报错状态码
    PRODUCT_DOES_NOTEXIST(1003, "商品不存在"),
    IrregularParticipation(47001, "入参格式不符合规范"),
    InputParameterError(200002, "入参错误"),
    ProhibitCreation(300001, "禁止创建/更新商品（如：商品创建功能被封禁)"),
    ProductNameIsTooLong(300002, "商品名称长度不符合规则"),
    ThePriceIsEnteredIncorrectly(300003, "价格输入不合规（如：现价比原价大、传入价格非数字等"),
    ProductNameHasIllegalContent(300004, "商品名称存在违规违法内容"),
    TheImageContainsIllegalContent(300005, "商品图片存在违规违法内容"),
    ImGUploadFailed(300006, "图片上传失败（如：mediaID过期）"),
    AppletsTheLinkDoesNotExist(300007, "线上小程序版本不存在该链接"),
    ADDSfail(300008, "添加商品失败"),
    WithdrawalFailed(300009, "商品审核撤回失败"),
    ApprovalStatus(300010, "商品审核状态不对（如：商品审核中）"),
    illegalOperation(300011, "操作非法（API不允许操作非API创建的商品）"),
    NoReviewLimit(300012, "没有提审额度（每天500次提审额度）"),
    ArraignmentFailed(300013, "提审失败"),
    UnderReview(300014, "审核中，无法删除（非零代表失败）"),
    NOTEXIST(300015, "商品不存在"),
    GoodsNotReviewed(300017, "商品未提审"),
    IMGOversize(300018, "商品图片尺寸过大"),
    ADDSUCCESS_AUDITFAILURE(300021, "商品添加成功，审核失败"),
    ;

    private final int code;
    private final String info;

    LiveGoodsErrorCodeEnum(int code, String info) {
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
        LiveGoodsErrorCodeEnum[] checkInEnums = LiveGoodsErrorCodeEnum.values();
        for (LiveGoodsErrorCodeEnum checkInEnum : checkInEnums) {
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
