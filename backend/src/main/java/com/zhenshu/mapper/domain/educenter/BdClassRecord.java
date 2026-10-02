package com.zhenshu.mapper.domain.educenter;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;


/**
 * 上课记录对象 bd_have_class_record
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_class_record")
public class BdClassRecord extends BaseEntity implements Serializable {
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
     * 对应班级
     */
    private Long classId;
    /**
     * 对应课程
     */
    private Long courseId;

    /**
     * 实到人数
     */
    private Integer realNumber;

    /**
     * 应到人数
     */
    private Integer shouldNumber;

    /**
     * 上课开始时间
     */
    private Date classStartTime;

    /**
     * 上课结束时间
     */
    private Date classEndTime;

    /**
     * 0:未删除1：已删除
     */
    private Integer isDelete;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 主教老师id
     */
    private Long bishopTeacherId;

    /**
     * 主教老师
     */
    private String bishopTeacherName;

    /**
     * 辅助老师
     */
    private String helpTeacherName;

    @Version
    private Integer version;

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getUpdateBy() {
        return updateBy;
    }

    @Override
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setRealNumber(Integer realNumber) {
        this.realNumber = realNumber;
    }

    public Integer getRealNumber() {
        return realNumber;
    }

    public void setShouldNumber(Integer shouldNumber) {
        this.shouldNumber = shouldNumber;
    }

    public Integer getShouldNumber() {
        return shouldNumber;
    }

    public void setClassStartTime(Date classStartTime) {
        this.classStartTime = classStartTime;
    }

    public Date getClassStartTime() {
        return classStartTime;
    }

    public void setClassEndTime(Date classEndTime) {
        this.classEndTime = classEndTime;
    }

    public Date getClassEndTime() {
        return classEndTime;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getBishopTeacherId() {
        return bishopTeacherId;
    }

    public void setBishopTeacherId(Long bishopTeacherId) {
        this.bishopTeacherId = bishopTeacherId;
    }

    public String getBishopTeacherName() {
        return bishopTeacherName;
    }

    public void setBishopTeacherName(String bishopTeacherName) {
        this.bishopTeacherName = bishopTeacherName;
    }

    public String getHelpTeacherName() {
        return helpTeacherName;
    }

    public void setHelpTeacherName(String helpTeacherName) {
        this.helpTeacherName = helpTeacherName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("classId", getClassId())
                .append("courseId", getCourseId())
                .append("realNumber", getRealNumber())
                .append("shouldNumber", getShouldNumber())
                .append("classStartTime", getClassStartTime())
                .append("classEndTime", getClassEndTime())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
