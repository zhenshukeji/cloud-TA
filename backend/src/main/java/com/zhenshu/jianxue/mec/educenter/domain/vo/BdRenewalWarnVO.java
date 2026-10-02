package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author yuxi
 */
@ApiModel("续费预警入参")
public class BdRenewalWarnVO extends InputEntity implements Serializable {

    @ApiModelProperty(value="学生名称")
    private String studentName;

    @ApiModelProperty(value="课程名称")
    private String courseName;

    @ApiModelProperty(value = "手机号")
    private String studentsPhone;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("课程id")
    private Long courseId;

    private Long mecId;

    private Long campusId;

    public Long getMecId() {
        return mecId;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentsPhone() {
        return studentsPhone;
    }

    public void setStudentsPhone(String studentsPhone) {
        this.studentsPhone = studentsPhone;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "BdRenewalWarnVO{" +
                "studentName='" + studentName + '\'' +
                ", courseName='" + courseName + '\'' +
                ", studentsPhone='" + studentsPhone + '\'' +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", mecId=" + mecId +
                ", campusId=" + campusId +
                '}';
    }
}
