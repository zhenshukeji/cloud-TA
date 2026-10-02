package com.zhenshu.mapper.domain.educenter;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 课程购买记录 对象 bd_course_purchase
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_course_purchase")
public class BdCoursePurchase extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机构id
     */
    private Long mecId;

    /**
     * 校区id
     */
    private Long campusId;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 课程id
     */
    private String courseName;

    /**
     * 课程封面图片
     */
    private String mainImg;

    /**
     * 课时数量
     */
    private Integer timeNum;

    /**
     * 购买价格（人民币元·线下收款登记）
     */
    private BigDecimal amountCny;

    /**
     * 状态 0=无效，1=正常
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除 0=未删除 1=删除
     */
    private Integer isDelete;


    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
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
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
