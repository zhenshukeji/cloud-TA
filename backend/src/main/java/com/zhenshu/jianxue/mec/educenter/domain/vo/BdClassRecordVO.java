package com.zhenshu.jianxue.mec.educenter.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

/**
 * 上课记录对象 bd_class_record
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("上课记录主体")
public class BdClassRecordVO extends InputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *  id
     */
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
     *  对应班级
     */
    @ApiModelProperty(value="对应班级", required = true)
    private Long classId;

    /**
     *  班级名称
     */
    @ApiModelProperty("班级名称")
    private String className;

    /**
     *  对应课程
     */
    @ApiModelProperty("对应课程")
    private Long courseId;

    /**
     *  课程名称

     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     *  实到人数
     */
    @ApiModelProperty("实到人数")
    private Integer realNumber;

    /**
     *  应到人数
     */
    @ApiModelProperty("应到人数")
    private Integer shouldNumber;

    /**
     *  上课开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("上课开始时间")
    private Date classStartTime;

    /**
     *  上课结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("上课结束时间")
    private Date classEndTime;

    /**
     *  0:未删除1：已删除
     */
    @ApiModelProperty(value="0:未删除1：已删除")
    private Integer isDelete;

    /**
     *  请假人员
     */
    @ApiModelProperty(value="请假人员", required = true)
    private List<BdAskLeaveVO> vacateList;

    /**
     *  点名信息
     */
    @ApiModelProperty(value="请假人员", required = true)
    private List<BdCallRecordVO> callRecordList;

    public List<BdAskLeaveVO> getVacateList() {
        return vacateList;
    }

    public void setVacateList(List<BdAskLeaveVO> vacateList) {
        this.vacateList = vacateList;
    }

    public List<BdCallRecordVO> getCallRecordList() {
        return callRecordList;
    }

    public void setCallRecordList(List<BdCallRecordVO> callRecordList) {
        this.callRecordList = callRecordList;
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

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setRealNumber(Integer realNumber) {
        this.realNumber = realNumber;
    }

    public Integer getRealNumber() {
        return realNumber;
    }

    public void setShouldNumber(Integer shouldNumber) {
        this.shouldNumber = shouldNumber;
    }

    public Integer getShouldNumber() {
        return shouldNumber;
    }

    public void setClassStartTime(Date classStartTime) {
        this.classStartTime = classStartTime;
    }

    public Date getClassStartTime() {
        return classStartTime;
    }

    public void setClassEndTime(Date classEndTime) {
        this.classEndTime = classEndTime;
    }

    public Date getClassEndTime() {
        return classEndTime;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("classId", getClassId())
                .append("className", getClassName())
                .append("courseId", getCourseId())
                .append("courseName", getCourseName())
                .append("realNumber", getRealNumber())
                .append("shouldNumber", getShouldNumber())
                .append("vacateList", getVacateList())
                .append("callRecordList", getCallRecordList())
                .append("shouldNumber", getShouldNumber())
                .append("classStartTime", getClassStartTime())
                .append("classEndTime", getClassEndTime())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
