package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("报读信息")
public class EnrollmentStatusBO extends OutputEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 学生姓名
     */
    @ApiModelProperty("学生姓名")
    private String studentName;

    /**
     * 学生手机号
     */
    @ApiModelProperty("学生手机号")
    private String phone;

    /**
     * 总课时
     */
    @ApiModelProperty("总课时")
    private Integer total;

    /**
     * 消课时
     */
    @ApiModelProperty("消课时")
    private Integer number;

    /**
     * 剩余课时
     */
    @ApiModelProperty("剩余课时")
    private Integer remainNumber;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 班级名称
     */
    @ApiModelProperty("班级名称")
    private String  className;

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

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getRemainNumber() {
        return remainNumber;
    }

    public void setRemainNumber(Integer remainNumber) {
        this.remainNumber = remainNumber;
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
}
