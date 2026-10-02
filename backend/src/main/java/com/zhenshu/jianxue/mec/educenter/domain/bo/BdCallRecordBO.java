package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;


/**
 * 课消记录对象 bd_call_record
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("课消记录主体")
public class BdCallRecordBO extends OutputEntity implements Serializable {
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
     *  班级id
     */
    @ApiModelProperty("班级id")
    private Long classId;

    /**
     *  班级名称
     */
    @ApiModelProperty("班级名称")
    private String className;

    /**
     *  课程id
     */
    @ApiModelProperty("课程id")
    private Long courseId;

    /**
     *  课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     *  学生id
     */
    @ApiModelProperty("学生id")
    private Long studentId;

    /**
     *  学生名称
     */
    @ApiModelProperty("学生名称")
    private String studentName;

    /**
     *  上课记录id
     */
    @ApiModelProperty("上课记录id")
    private Long classRecordId;

    /**
     *  消课课时
     */
    @ApiModelProperty("消课课时")
    private Integer classTimeNum;

    /**
     *  0:到课 1:迟到 2：请假 3:未到4提前请假
     */
    @ApiModelProperty("0:到课 1:迟到 2：请假 3:未到4提前请假")
    private Integer type;

    /**
     *  是否删除
     */
    @ApiModelProperty("是否删除")
    private Integer isDelete;

    /**
     *  上课开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("上课开始时间")
    private String classStartTime;

    /**
     *  上课结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("上课结束时间")
    private String classEndTime;

    /**
     * 上课时间
     */
    @ApiModelProperty("上课时间")
    private String classTime;

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassStartTime() {
        return classStartTime;
    }

    public void setClassStartTime(String classStartTime) {
        this.classStartTime = classStartTime;
    }

    public String getClassEndTime() {
        return classEndTime;
    }

    public void setClassEndTime(String classEndTime) {
        this.classEndTime = classEndTime;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setClassRecordId(Long classRecordId) {
        this.classRecordId = classRecordId;
    }

    public Long getClassRecordId() {
        return classRecordId;
    }

    public void setClassTimeNum(Integer classTimeNum) {
        this.classTimeNum = classTimeNum;
    }

    public Integer getClassTimeNum() {
        return classTimeNum;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("classId", getClassId())
                .append("studentId", getStudentId())
                .append("classRecordId", getClassRecordId())
                .append("classTimeNum", getClassTimeNum())
                .append("type", getType())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
