package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 课时 对象 bd_course_time
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("课时 主体")
public class BdCourseTimeVO extends InputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 学生id
     */
    @ApiModelProperty(value = "学生id")
    private Long studentId;

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
     * 课程id
     */
    @ApiModelProperty("课程id")
    private Long courseId;

    /**
     * 总课时
     */
    @ApiModelProperty("总课时")
    private Integer totalNumber;

    /**
     * 剩余课时
     */
    @ApiModelProperty("剩余课时")
    private Integer remainingNumber;

    /**
     * 0:未删除1：已删除
     */
    @ApiModelProperty("0:未删除1：已删除")
    private Integer isDelete;

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
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

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setRemainingNumber(Integer remainingNumber) {
        this.remainingNumber = remainingNumber;
    }

    public Integer getRemainingNumber() {
        return remainingNumber;
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
                .append("studentId", getStudentId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("courseId", getCourseId())
                .append("totalNumber", getTotalNumber())
                .append("remainingNumber", getRemainingNumber())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
