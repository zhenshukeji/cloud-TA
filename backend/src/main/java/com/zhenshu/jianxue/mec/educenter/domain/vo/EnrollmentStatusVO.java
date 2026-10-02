package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("报读信息")
public class EnrollmentStatusVO extends InputEntity {

    /**
     * 学生姓名
     */
    @ApiModelProperty("studentName")
    private String studentName;

    /**
     * 学生手机号
     */
    @ApiModelProperty("phone")
    private String phone;

    /**
     * 课程名称
     */
    @ApiModelProperty("courseName")
    private String courseName;

    /**
     * 班级名称
     */
    @ApiModelProperty("className")
    private String className;

    @ApiModelProperty("start")
    private Integer start;

    @ApiModelProperty("mecId")
    private Long mecId;

    @ApiModelProperty("campusId")
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

}
