package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;


/**
 * 班级对象 bd_class
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("班级主体")
public class BdClassBO extends OutputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *  id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     *  机构id
     */
    @ApiModelProperty("机构id")
    private Long mecId;

    /**
     *  校区id
     */
    @ApiModelProperty("校区id")
    private Long campusId;

    /**
     *  已授课时
     */
    @ApiModelProperty("已授课时")
    private Integer hasClass;

    /**
     *  已排课次
     */
    @ApiModelProperty("已排课次")
    private Integer haveClass;

    /**
     *  主教老师名称
     */
    @ApiModelProperty("主教老师名称")
    private String bishopTeacherName;

    /**
     *  主教老师
     */
    @ApiModelProperty("主教老师")
    private Long bishopTeacherId;

    /**
     *  辅助老师名称
     */
    @ApiModelProperty("辅助老师名称")
    private String helpTeacherName;

    /**
     *  辅助老师
     */
    @ApiModelProperty("辅助老师")
    private Long helpTeacherId;

    /**
     *  容量
     */
    @ApiModelProperty("容量")
    private Integer sumStudents;

    /**
     *  已有学员
     */
    @ApiModelProperty("已有学员")
    private Integer existStudents;

    /**
     *  班级名称
     */
    @ApiModelProperty("班级名称")
    private String className;

    /**
     *  关联课程
     */
    @ApiModelProperty("关联课程")
    private Long classCourseId;

    /**
     *  课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     *  是否删除 0=未删除 1=删除
     */
    @ApiModelProperty("是否删除 0=未删除 1=删除")
    private Integer isDelete;

    /**
     *  班级类型 0：体验  1：常规
     */
    @ApiModelProperty("班级类型 0：体验  1：常规")
    private Integer classType;

    /**
     *  班级状态 0：已结业 1：未结业
     */
    @ApiModelProperty("班级状态 0：已结业 1：未结业")
    private Integer classState;

    /**
     *  上课时间
     */
    @ApiModelProperty("上课时间")
    private List<String> classTimes;

    /**
     *  班级上课时间集合
     */
    @ApiModelProperty("班级上课时间集合")
    private List<BdClassTimeBO> classTimeList;

    private Integer taughtStuNum;

    private String courseColor;

    public String getCourseColor() {
        return courseColor;
    }

    public void setCourseColor(String courseColor) {
        this.courseColor = courseColor;
    }

    public Integer getTaughtStuNum() {
        return taughtStuNum;
    }

    public void setTaughtStuNum(Integer taughtStuNum) {
        this.taughtStuNum = taughtStuNum;
    }

    public List<BdClassTimeBO> getClassTimeList() {
        return classTimeList;
    }

    public void setClassTimeList(List<BdClassTimeBO> classTimeList) {
        this.classTimeList = classTimeList;
    }

    public List<String> getClassTimes() {
        return classTimes;
    }

    public void setClassTimes(List<String> classTimes) {
        this.classTimes = classTimes;
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

    public void setBishopTeacherName(String bishopTeacherName) {
        this.bishopTeacherName = bishopTeacherName;
    }

    public String getBishopTeacherName() {
        return bishopTeacherName;
    }

    public void setBishopTeacherId(Long bishopTeacherId) {
        this.bishopTeacherId = bishopTeacherId;
    }

    public Long getBishopTeacherId() {
        return bishopTeacherId;
    }

    public void setHelpTeacherName(String helpTeacherName) {
        this.helpTeacherName = helpTeacherName;
    }

    public String getHelpTeacherName() {
        return helpTeacherName;
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

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
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
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("hasClass", getHasClass())
                .append("haveClass", getHaveClass())
                .append("bishopTeacherName", getBishopTeacherName())
                .append("bishopTeacherId", getBishopTeacherId())
                .append("helpTeacherName", getHelpTeacherName())
                .append("helpTeacherId", getHelpTeacherId())
                .append("sumStudents", getSumStudents())
                .append("existStudents", getExistStudents())
                .append("className", getClassName())
                .append("classCourseId", getClassCourseId())
                .append("courseName", getCourseName())
                .append("classTime", getClassTimes())
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
