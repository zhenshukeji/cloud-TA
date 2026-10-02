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
 * 学生请假对象 bd_ask_leave
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("学生请假主体")
public class BdAskLeaveBO extends OutputEntity implements Serializable {
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
     *  请假课程
     */
    @ApiModelProperty("请假课程")
    private String courseName;

    /**
     *  班级名称
     */
    @ApiModelProperty("班级名称")
    private String className;

    /**
     *  请假时间开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("请假时间开始")
    private Date startTime;

    /**
     *  请假时间结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("请假时间结束")
    private Date offTime;

    /**
     *  申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("申请时间")
    private Date applyTime;

    /**
     *  处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("处理时间")
    private Date disposeTime;

    /**
     *  请假原因
     */
    @ApiModelProperty("请假原因")
    private String leaveReason;

    /**
     *  拒绝原因
     */
    @ApiModelProperty("拒绝原因")
    private String refuseReason;

    /**
     *  审核状态 0:已通过  1：未通过  2：未审核
     */
    @ApiModelProperty("审核状态 0:已通过  1：未通过  2：未审核")
    private Integer type;

    /**
     *  是否删除 0:未删除1：已删除
     */
    @ApiModelProperty("是否删除 0:未删除1：已删除")
    private Integer isDelete;

    /**
     *  课消表id
     */
    @ApiModelProperty("课消表id")
    private Long callRecordId;

    /**
     *  上课记录id
     */
    @ApiModelProperty("上课记录id")
    private Long classRecordId;

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getOffTime() {
        return offTime;
    }

    public void setOffTime(Date offTime) {
        this.offTime = offTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setDisposeTime(Date disposeTime) {
        this.disposeTime = disposeTime;
    }

    public Date getDisposeTime() {
        return disposeTime;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setRefuseReason(String refuseReason) {
        this.refuseReason = refuseReason;
    }

    public String getRefuseReason() {
        return refuseReason;
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

    public void setCallRecordId(Long callRecordId) {
        this.callRecordId = callRecordId;
    }

    public Long getCallRecordId() {
        return callRecordId;
    }

    public void setClassRecordId(Long classRecordId) {
        this.classRecordId = classRecordId;
    }

    public Long getClassRecordId() {
        return classRecordId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("studentId", getStudentId())
                .append("studentName", getStudentName())
                .append("className", getClassName())
                .append("courseName", getCourseName())
                .append("startTime", getStartTime())
                .append("offTime", getOffTime())
                .append("applyTime", getApplyTime())
                .append("disposeTime", getDisposeTime())
                .append("leaveReason", getLeaveReason())
                .append("refuseReason", getRefuseReason())
                .append("type", getType())
                .append("isDelete", getIsDelete())
                .append("callRecordId", getCallRecordId())
                .append("classRecordId", getClassRecordId())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
