package com.zhenshu.common.enums.website;

import java.util.HashMap;

/**
 * @author yuxi
 * @version 1.0
 * @date 2021/1/5 19:59
 * @desc 校区资讯
 */
public enum CampusInfoEnum {
    /**
     * 审核状态
     * 3=审核未通过
     * 2=审核通过
     * 1=未审核
     */
    FAILED_AUDIT(3, "审核未通过"),
    PASS_AUDIT(2, "审核通过"),
    UN_AUDIT(1, "未审核");

    public static boolean isExist(int status){
        CampusInfoEnum[] values = CampusInfoEnum.values();
        HashMap<Integer, CampusInfoEnum> map = new HashMap<>();
        for (CampusInfoEnum campusInfoEnum : values){
            map.put(campusInfoEnum.getCode(), campusInfoEnum);
        }
        return map.containsKey(status);
    }

    private final int code;
    private final String info;

    CampusInfoEnum(int code, String info) {
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
