package com.zhenshu.jianxue.mec.statistics.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;

/**
 * @author jing
 * @version 1.0
 * @desc 班级入参
 * @date 2020/11/12 0012 18:14
 **/
public class BdClassDetailVO extends InputEntity {

    /**
     * 机构id
     */
    private Long mecId;

    /**
     * 校区id
     */
    private Long campusId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 主教老师名称
     */
    private String bishopTeacherName;

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public Long getMecId() {
        return mecId;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBishopTeacherName() {
        return bishopTeacherName;
    }

    public void setBishopTeacherName(String bishopTeacherName) {
        this.bishopTeacherName = bishopTeacherName;
    }
}
