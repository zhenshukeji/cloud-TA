package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;


/**
 * 课时修改记录 对象 bd_course_time_update
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("课时修改记录 主体")
public class BdCourseTimeUpdateBO extends OutputEntity implements Serializable {
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
     * 校区id
     */
    @ApiModelProperty("校区id")
    private Long campusId;

    /**
     * 学生
     */
    @ApiModelProperty("学生")
    private Long studentId;

    /**
     * 课程课时id
     */
    @ApiModelProperty("课程课时id")
    private Long courseTimeId;


    /**
     * 课程id
     */
    @ApiModelProperty("课程id")
    private Long courseId;

    /**
     * 修改类型0新增1删除
     */
    @ApiModelProperty("修改类型0新增1删除")
    private Integer changeType;

    /**
     * 数量
     */
    @ApiModelProperty("数量")
    private Integer num;

    /**
     * 操作原因
     */
    @ApiModelProperty("操作原因")
    private String reason;

    /**
     * 操作类型1工作人员手动增加课时2课时购买记录3.账户购买课程
     */
    @ApiModelProperty("操作类型1工作人员手动增加课时2课时购买记录3.账户购买课程")
    private Integer type;

    /**
     * 0:未删除1：已删除
     */
    @ApiModelProperty("0:未删除1：已删除")
    private Integer isDelete;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public void setCourseTimeId(Long courseTimeId) {
        this.courseTimeId = courseTimeId;
    }

    public Long getCourseTimeId() {
        return courseTimeId;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getNum() {
        return num;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("studentId", getStudentId())
                .append("courseTimeId", getCourseTimeId())
                .append("changeType", getChangeType())
                .append("num", getNum())
                .append("reason", getReason())
                .append("type", getType())
                .append("isDelete", getIsDelete())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
