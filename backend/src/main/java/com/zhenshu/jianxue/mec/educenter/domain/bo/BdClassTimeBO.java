package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.zhenshu.framework.aspectj.lang.annotation.Excel;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;


/**
 * 班级上课时间对象 bd_class_time
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("班级上课时间主体")
public class BdClassTimeBO extends OutputEntity implements Serializable {
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
     * 周几
     */
    @ApiModelProperty("周几")
    private Integer weekDay;

    /**
     * 上课开始时间
     */
    @ApiModelProperty("上课开始时间")
    private String clsStartTime;

    /**
     * 上课结束时间
     */
    @ApiModelProperty("上课结束时间")
    private String clsEndTime;

    /**
     * 0:未删除1：已删除
     */
    @ApiModelProperty("0:未删除1：已删除")
    private Integer isDelete;

    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称")
    private String className;

    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;

    /**
     * 课程ID
     */
    @ApiModelProperty(value = "课程ID")
    private Long courseId;

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
     * 学生名称集合
     */
    @Excel(name = "学生名称集合")
    @ApiModelProperty("学生名称集合")
    private List<String> stuNameList;


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
     * 这周该节课上课开始时间
     */
    @ApiModelProperty(value = "这周该节课上课开始时间")
    private String startInWeek ;

    /**
     * 这周该节课上课结束时间
     */
    @ApiModelProperty(value = "这周该节课上课结束时间")
    private String endInWeek;

    /**
     * 课程颜色
     */
    @ApiModelProperty(value = "课程颜色")
    private String courseColor;

    /**
     * id类型 0=班级上课 1=调课
     */
    @ApiModelProperty(value = "id类型 0=班级上课 1=调课")
    private Integer idType;


    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public String getCourseColor() {
        return courseColor;
    }

    public void setCourseColor(String courseColor) {
        this.courseColor = courseColor;
    }

    public String getStartInWeek() {
        return startInWeek;
    }

    public void setStartInWeek(String startInWeek) {
        this.startInWeek = startInWeek;
    }

    public String getEndInWeek() {
        return endInWeek;
    }

    public void setEndInWeek(String endInWeek) {
        this.endInWeek = endInWeek;
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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public List<String> getStuNameList() {
        return stuNameList;
    }

    public void setStuNameList(List<String> stuNameList) {
        this.stuNameList = stuNameList;
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

    public void setBishopTeacherName(String bishopTeacherName) {
        this.bishopTeacherName = bishopTeacherName;
    }

    public String getBishopTeacherName() {
        return bishopTeacherName;
    }

    public void setHelpTeacherName(String helpTeacherName) {
        this.helpTeacherName = helpTeacherName;
    }

    public String getHelpTeacherName() {
        return helpTeacherName;
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

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setClsStartTime(String clsStartTime) {
        this.clsStartTime = clsStartTime;
    }

    public String getClsStartTime() {
        return clsStartTime;
    }

    public void setClsEndTime(String clsEndTime) {
        this.clsEndTime = clsEndTime;
    }

    public String getClsEndTime() {
        return clsEndTime;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("classId", getClassId())
                .append("weekDay", getWeekDay())
                .append("clsStartTime", getClsStartTime())
                .append("clsEndTime", getClsEndTime())
                .append("courseName", getCourseName())
                .append("className", getClassName())
                .append("bishopTeacherName", getBishopTeacherName())
                .append("helpTeacherName", getHelpTeacherName())
                .append("startInWeek", getStartInWeek())
                .append("endInWeek", getEndInWeek())
                .append("stuNameList", getStuNameList())
                .append("courseId", getCourseId())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
