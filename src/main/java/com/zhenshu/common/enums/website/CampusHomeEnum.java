package com.zhenshu.common.enums.website;

import java.util.HashMap;

/**
 * @author yuxi
 * @version 1.0
 * @date 2021/1/5 18:06
 * @desc 校区主页
 */
public enum CampusHomeEnum {

    /**
     * 审核状态
     * 3=审核未通过
     * 2=审核通过
     * 1=未审核
     * 0=未提交
     */
    FAILED_AUDIT(3, "审核未通过"),
    PASS_AUDIT(2, "审核通过"),
    UN_AUDIT(1, "未审核"),
    UN_SUBMIT(0, "未提交");

    private final int code;
    private final String info;

    CampusHomeEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public static boolean isExist(int status){
        CampusHomeEnum[] values = CampusHomeEnum.values();
        HashMap<Integer, CampusHomeEnum> map = new HashMap<>();
        for (CampusHomeEnum campusHomeEnum : values){
            map.put(campusHomeEnum.getCode(), campusHomeEnum);
        }
        return map.containsKey(status);
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
