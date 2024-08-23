package com.zhenshu.common.enums;

/**
 * @author yuxi
 * @version 1.0
 * @date 2021/1/14 13:39
 * @desc 性别枚举
 */
public enum SexEnums {
    /**
     * 性别
     */
    MAN(0, "男"), WOMAN(1, "女"), UN_KNOWN(2, "未知");

    private final int code;
    private final String info;

    SexEnums(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    /**
     * 获得0,1,2的性别类型
     * @param info 男，女，未知性别
     * @return 0,1,2
     */
    public static Integer getCodeByInfo(String info){
        String sex = info.trim();
        SexEnums[] values = SexEnums.values();
        for (SexEnums sexEnums : values){
            if (sexEnums.getInfo().equals(sex)){
                return sexEnums.getCode();
            }
        }
        return 2;
    }
}
