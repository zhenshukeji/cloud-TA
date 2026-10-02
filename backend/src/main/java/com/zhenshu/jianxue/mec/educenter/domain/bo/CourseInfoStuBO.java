package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("学生详细信息中课时信息")
public class CourseInfoStuBO extends OutputEntity {

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 班级名称
     */
    @ApiModelProperty("班级名称")
    private String className;

    /**
     * 剩余课时
     */
    @ApiModelProperty("剩余课时")
    private Integer remainingNumber;

    /**
     * 总课时
     */
    @ApiModelProperty("总课时")
    private Integer totalNumber;

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getRemainingNumber() {
        return remainingNumber;
    }

    public void setRemainingNumber(Integer remainingNumber) {
        this.remainingNumber = remainingNumber;
    }
}
