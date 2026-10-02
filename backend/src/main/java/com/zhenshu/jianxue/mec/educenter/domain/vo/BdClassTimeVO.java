package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;

/**
 * 班级上课时间 对象 bd_class_time
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("班级上课时间主体")
public class BdClassTimeVO extends InputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *  id
     */
    @ApiModelProperty(value = "id", required = true)
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
    @ApiModelProperty(value="班级id")
    private Long classId;

    /**
     *  周几
     */
    @ApiModelProperty(value="周几")
    private Integer weekDay;

    /**
     *  上课开始时间
     */
    @ApiModelProperty(value="上课开始时间")
    private String clsStartTime;

    /**
     *  上课结束时间
     */
    @ApiModelProperty(value="上课结束时间")
    private String clsEndTime;

    /**
     *  0:未删除1：已删除
     */
    @ApiModelProperty(value="0:未删除1：已删除")
    private Integer isDelete;

    /**
     *  班级名称
     */
    @ApiModelProperty(value="班级名称")
    private String className;

    /**
     *  课程名称
     */
    @ApiModelProperty(value="课程名称")
    private String courseName;

    /**
     * 这周该节课上课开始时间，进入点名记录时必传
     */
    @ApiModelProperty(value = "这周该节课上课开始时间", required = true)
    private String startInWeek ;

    /**
     * 这周该节课上课结束时间，进入点名记录时必传
     */
    @ApiModelProperty(value = "这周该节课上课结束时间", required = true)
    private String endInWeek;

    /**
     * id类型 0=班级上课时间表id 1=调课记录id
     */
    @ApiModelProperty(value="id类型 0=班级上课时间表id 1=调课记录id")
    private Long idType;

    /**
     *  原本上课开始时间
     */
    @ApiModelProperty("原本开始上课时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date originalStartTime;

    /**
     *  原本上课结束时间
     */
    @ApiModelProperty("原本上课结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date originalEndTime;

    /**
     *  原本上课结束时间
     */
    @ApiModelProperty("学生名称")
    private String studentName;

    public Date getOriginalStartTime() {
        return originalStartTime;
    }

    public void setOriginalStartTime(Date originalStartTime) {
        this.originalStartTime = originalStartTime;
    }

    public Date getOriginalEndTime() {
        return originalEndTime;
    }

    public void setOriginalEndTime(Date originalEndTime) {
        this.originalEndTime = originalEndTime;
    }

    public Long getIdType() {
        return idType;
    }

    public void setIdType(Long idType) {
        this.idType = idType;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("classId", getClassId())
                .append("weekDay", getWeekDay())
                .append("clsStartTime", getClsStartTime())
                .append("clsEndTime", getClsEndTime())
                .append("courseName", getCourseName())
                .append("className", getClassName())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
