package com.zhenshu.mapper.domain.educenter;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.annotation.Version;

import java.io.Serializable;


/**
 * 班级对象 bd_class
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_class")
public class BdClass extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机构id
     */
    private Long mecId;

    /**
     * 校区id
     */
    private Long campusId;

    /**
     * 已授课时
     */
    private Integer hasClass;

    /**
     * 已排课次
     */
    private Integer haveClass;

    /**
     * 主教老师
     */
    private Long bishopTeacherId;

    /**
     * 辅助老师
     */
    private Long helpTeacherId;

    /**
     * 容量
     */
    private Integer sumStudents;

    /**
     * 已有学员
     */
    private Integer existStudents;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 关联课程
     */
    private Long classCourseId;

    /**
     * 是否删除 0=未删除 1=删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 班级类型 0：体验  1：常规
     */
    private Integer classType;

    /**
     * 班级状态 0：已结业 1：未结业
     */
    private Integer classState;

    @Version
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public Long getMecId() {
        return mecId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setHasClass(Integer hasClass) {
        this.hasClass = hasClass;
    }

    public Integer getHasClass() {
        return hasClass;
    }

    public void setHaveClass(Integer haveClass) {
        this.haveClass = haveClass;
    }

    public Integer getHaveClass() {
        return haveClass;
    }

    public void setBishopTeacherId(Long bishopTeacherId) {
        this.bishopTeacherId = bishopTeacherId;
    }

    public Long getBishopTeacherId() {
        return bishopTeacherId;
    }

    public void setHelpTeacherId(Long helpTeacherId) {
        this.helpTeacherId = helpTeacherId;
    }

    public Long getHelpTeacherId() {
        return helpTeacherId;
    }

    public void setSumStudents(Integer sumStudents) {
        this.sumStudents = sumStudents;
    }

    public Integer getSumStudents() {
        return sumStudents;
    }

    public void setExistStudents(Integer existStudents) {
        this.existStudents = existStudents;
    }

    public Integer getExistStudents() {
        return existStudents;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassCourseId(Long classCourseId) {
        this.classCourseId = classCourseId;
    }

    public Long getClassCourseId() {
        return classCourseId;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setClassType(Integer classType) {
        this.classType = classType;
    }

    public Integer getClassType() {
        return classType;
    }

    public void setClassState(Integer classState) {
        this.classState = classState;
    }

    public Integer getClassState() {
        return classState;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("hasClass", getHasClass())
                .append("haveClass", getHaveClass())
                .append("bishopTeacherId", getBishopTeacherId())
                .append("helpTeacherId", getHelpTeacherId())
                .append("sumStudents", getSumStudents())
                .append("existStudents", getExistStudents())
                .append("className", getClassName())
                .append("classCourseId", getClassCourseId())
                .append("isDelete", getIsDelete())
                .append("classType", getClassType())
                .append("classState", getClassState())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
