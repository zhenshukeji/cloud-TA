package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;


/**
 * 学生班级中间对象 bd_student_class
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("学生班级中间主体")
public class BdStudentClassBO extends OutputEntity implements Serializable {
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
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String mecName;

    /**
     * 校区id
     */
    @ApiModelProperty("校区id")
    private Long campusId;

    /**
     * 校区名称
     */
    @ApiModelProperty("校区名称")
    private String campusName;

    /**
     * 学生id
     */
    @ApiModelProperty("学生id")
    private Long studentId;

    /**
     * 学生名称
     */
    @ApiModelProperty("学生名称")
    private String studentName;

    /**
     * 学生手机号
     */
    @ApiModelProperty("学生手机号")
    private String studentsPhone;

    /**
     * 班级id
     */
    @ApiModelProperty("班级id")
    private Long classId;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("添加时间")
    private Date haveTime;

    /**
     * 0：没有删除1：已删除
     */
    @ApiModelProperty("0：没有删除1：已删除")
    private Integer isDelete;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("班级总人数")
    private Integer totalStu;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("课程id")
    private Long courseId;

    @ApiModelProperty("公总号绑定openid")
    private String wechatSubscribeOpenid;

    public Integer getTotalStu() {
        return totalStu;
    }

    public void setTotalStu(Integer totalStu) {
        this.totalStu = totalStu;
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

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setHaveTime(Date haveTime) {
        this.haveTime = haveTime;
    }

    public Date getHaveTime() {
        return haveTime;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentsPhone() {
        return studentsPhone;
    }

    public void setStudentsPhone(String studentsPhone) {
        this.studentsPhone = studentsPhone;
    }

    public String getMecName() {
        return mecName;
    }

    public void setMecName(String mecName) {
        this.mecName = mecName;
    }

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getWechatSubscribeOpenid() {
        return wechatSubscribeOpenid;
    }

    public void setWechatSubscribeOpenid(String wechatSubscribeOpenid) {
        this.wechatSubscribeOpenid = wechatSubscribeOpenid;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("studentId", getStudentId())
                .append("classId", getClassId())
                .append("haveTime", getHaveTime())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
