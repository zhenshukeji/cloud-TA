package com.zhenshu.common.enums.educenter;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/10/24 16:25
 * @desc 学生信息枚举类
 */
public enum StudentEnum {

    /**
     *
     */
    NORMAL_STU(0, "正常学员"),
    RECYCLE_STU(1, "回收站学员"),
    /**
     * 导入
     */
    NAME(0, "学员姓名"),
    PHONE(1, "学员手机号"),
    GRACE(2, "学员年级"),
    SEX(3, "性别"),
    AGE(4, "学员年龄"),
    SCHOOL(5, "学员学校/幼儿园"),
    ADDRESS(6, "学员地址"),
    RELATION(7, "学员关系"),
    RELATION_NAME(8, "学员关系者姓名"),
    RELATION_PHONE(9, "学员关系者电话"),

    /**
     * 关系 0=父亲 1=母亲 2=爷爷 3=奶奶 4=外公 5=外婆 6=其他
     */
    RELATION_FATHER(0,"父亲"),
    RELATION_MOTHER(1,"母亲"),
    RELATION_GRANDPA_FATHER(2,"爷爷"),
    RELATION_GRANDMA_FATHER(3,"奶奶"),
    RELATION_GRANDPA_MOTHER(4,"外公"),
    RELATION_GRANDMA_MOTHER(5,"外婆"),
    RELATION_OTHER(6,"其他");


    private final int code;
    private final String info;

    StudentEnum(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public static Integer getRelation(String relation){
        StudentEnum[] studentEnums = values();
        for (StudentEnum studentEnum : studentEnums){
            if (relation.equals(studentEnum.getInfo())){
                return studentEnum.getCode();
            }
        }
        return null;
    }
}
