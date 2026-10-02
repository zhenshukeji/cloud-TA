package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 续费预警
 *
 * @author yuxi
 * @date 2020-10-04
 */
@ApiModel("续费预警主体")
public class BdRenewalWarnBO extends OutputEntity implements Serializable {

    @ApiModelProperty("课时id")
    private Long id;

    @ApiModelProperty("学生名称")
    private String studentName;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("学生openid")
    private String openid;

    @ApiModelProperty("学生手机号")
    private String studentsPhone;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("课程id")
    private Long courseId;

    @ApiModelProperty("总课时")
    private Integer totalNumber;

    @ApiModelProperty("剩余课时")
    private Integer remainingNumber;

    @ApiModelProperty("上次提醒时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentsPhone() {
        return studentsPhone;
    }

    public void setStudentsPhone(String studentsPhone) {
        this.studentsPhone = studentsPhone;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getRemainingNumber() {
        return remainingNumber;
    }

    public void setRemainingNumber(Integer remainingNumber) {
        this.remainingNumber = remainingNumber;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "BdRenewalWarnBO{" +
                "id=" + id +
                ", studentName='" + studentName + '\'' +
                ", studentId=" + studentId +
                ", openid='" + openid + '\'' +
                ", studentsPhone='" + studentsPhone + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseId=" + courseId +
                ", totalNumber=" + totalNumber +
                ", remainingNumber=" + remainingNumber +
                '}';
    }
}
