package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 上课临时更改记录 对象 bd_class_change
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("上课临时更改记录 主体")
public class BdClassChangeBO extends OutputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 机构id
     */
    @ApiModelProperty("机构id")
    private Long mecId;

    /**
     * 校区id
     */
    @ApiModelProperty("校区id")
    private Long campusId;

    /**
     * 班级id
     */
    @ApiModelProperty("班级id")
    private Long classId;

    /**
     * 班级id
     */
    @ApiModelProperty("班级名称")
    private String className;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;


    /**
     * 主教老师名称
     */
    @ApiModelProperty("主教老师名称")
    private String bishopTeacherName;

    /**
     * 辅助老师名称
     */
    @ApiModelProperty("辅助老师名称")
    private String helpTeacherName;

    /**
     * 更改类型0取消1新增2调课
     */
    @ApiModelProperty("更改类型0取消1新增2调课")
    private Integer type;

    /**
     * 班级上课时间id（调课）
     */
    @ApiModelProperty("班级上课时间id（调课）")
    private Long classTimeId;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date clsStartTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date clsEndTime;

    /**
     * 是否删除
     */
    @ApiModelProperty("是否删除")
    private Integer isDelete;

    /**
     * 总人数
     */
    @ApiModelProperty(value = "总人数")
    private Integer sumStudents;

    /**
     * 已有人数
     */
    @ApiModelProperty(value = "已有人数")
    private Integer existStudents;

    /**
     * 该班级已有学生
     */
    @ApiModelProperty(value = "该班级已有学生")
    private List<String> stuNames;

    /**
     * 原本上课开始时间
     */
    @ApiModelProperty("原本开始上课时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date originalStartTime;

    /**
     * 原本上课结束时间
     */
    @ApiModelProperty("原本上课结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date originalEndTime;

    /**
     * 课程颜色
     */
    @ApiModelProperty(value = "课程颜色")
    private String courseColor;

    public String getCourseColor() {
        return courseColor;
    }

    public void setCourseColor(String courseColor) {
        this.courseColor = courseColor;
    }

    public Date getOriginalStartTime() {
        return originalStartTime;
    }

    public void setOriginalStartTime(Date originalStartTime) {
        this.originalStartTime = originalStartTime;
    }

    public Date getOriginalEndTime() {
        return originalEndTime;
    }

    public void setOriginalEndTime(Date originalEndTime) {
        this.originalEndTime = originalEndTime;
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

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setClassTimeId(Long classTimeId) {
        this.classTimeId = classTimeId;
    }

    public Long getClassTimeId() {
        return classTimeId;
    }

    public Date getClsStartTime() {
        return clsStartTime;
    }

    public void setClsStartTime(Date clsStartTime) {
        this.clsStartTime = clsStartTime;
    }

    public Date getClsEndTime() {
        return clsEndTime;
    }

    public void setClsEndTime(Date clsEndTime) {
        this.clsEndTime = clsEndTime;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public Integer getSumStudents() {
        return sumStudents;
    }

    public void setSumStudents(Integer sumStudents) {
        this.sumStudents = sumStudents;
    }

    public Integer getExistStudents() {
        return existStudents;
    }

    public void setExistStudents(Integer existStudents) {
        this.existStudents = existStudents;
    }

    public List<String> getStuNames() {
        return stuNames;
    }

    public void setStuNames(List<String> stuNames) {
        this.stuNames = stuNames;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("classId", getClassId())
                .append("type", getType())
                .append("classTimeId", getClassTimeId())
                .append("clsStartTime", getClsStartTime())
                .append("courseName", getCourseName())
                .append("className", getClassName())
                .append("clsEndTime", getClsEndTime())
                .append("isDelete", getIsDelete())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
