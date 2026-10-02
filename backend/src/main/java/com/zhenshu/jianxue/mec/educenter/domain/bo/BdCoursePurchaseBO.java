package com.zhenshu.jianxue.mec.educenter.domain.bo;

import java.math.BigDecimal;

import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;


/**
 * 课程购买记录 对象 bd_course_purchase
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("课程购买记录 主体")
public class BdCoursePurchaseBO extends OutputEntity implements Serializable {
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
     *  课程id
     */
    @ApiModelProperty("课程id")
    private Long courseId;

    /**
     *  课时数量
     */
    @ApiModelProperty("课时数量")
    private Integer timeNum;

    /**
     *  购买价格（人民币元·线下收款登记）
     */
    @ApiModelProperty("购买价格（人民币元·线下收款登记）")
    private BigDecimal amountCny;

    /**
     *  是否删除 0=未删除 1=删除
     */
    @ApiModelProperty("是否删除 0=未删除 1=删除")
    private Integer isDelete;

    /**
     *  备注
     */
    @ApiModelProperty("备注")
    private String remark;

    //add by yixi

    /**
     *  课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     *  操作人
     */
    @ApiModelProperty("操作人")
    private String operationName;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
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

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setTimeNum(Integer timeNum) {
        this.timeNum = timeNum;
    }

    public Integer getTimeNum() {
        return timeNum;
    }

    public void setAmountCny(BigDecimal amountCny) {
        this.amountCny = amountCny;
    }

    public BigDecimal getAmountCny() {
        return amountCny;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("studentId", getStudentId())
                .append("courseId", getCourseId())
                .append("timeNum", getTimeNum())
                .append("amountCny", getAmountCny())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("coureName", getCourseName())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
