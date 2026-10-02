package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 课程对象 bd_course
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("课程主体")
public class BdCourseVO extends InputEntity implements Serializable {
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
     *  课程价格（人民币元·线下收款登记）
     */
    @ApiModelProperty(value="课程价格（人民币元·线下收款登记）", required = true)
    private BigDecimal amountCny;

    /**
     *  实收价格（人民币元·线下收款登记）
     */
    @ApiModelProperty("实收价格（人民币元·线下收款登记）")
    private BigDecimal paidAmountCny;

    /**
     *  课时数量
     */
    @ApiModelProperty(value="课时数量", required = true)
    private Integer timeNum;

    /**
     *  礼品赠送
     */
    @ApiModelProperty("礼品赠送")
    private String gift;

    /**
     *  课程名称
     */
    @ApiModelProperty(value="课程名称", required = true)
    private String courseName;

    /**
     * 课程封面图片
     */
    @ApiModelProperty("课程封面图片")
    private String coverImage;

    /**
     * 课程封面小图片
     */
    @ApiModelProperty("课程封面小图片")
    private String minCoverImage;

    /**
     *  所属科目
     */
    @ApiModelProperty(value="所属科目", required = true)
    private String subject;

    /**
     *  课表颜色
     */
    @ApiModelProperty("课表颜色")
    private String color;

    /**
     *  课程状态 0=下架 1=上架
     */
    @ApiModelProperty(value="课程状态 0=下架 1=上架", required = true)
    private Integer isShelf;

    /**
     *  课程类型 0=体验  1=常规
     */
    @ApiModelProperty(value="课程类型 0=体验  1=常规", required = true)
    private Integer isExperience;

    /**
     *  课程介绍
     */
    @ApiModelProperty(value="课程介绍", required = true)
    private String introduction;

    /**
     *  是否删除 0=未删除 1=已删除
     */
    @ApiModelProperty("是否删除 0=未删除 1=已删除")
    private Integer isDelete;

    /**
     * 简介
     */
    @ApiModelProperty("简介")
    private String describe;

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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

    public void setAmountCny(BigDecimal amountCny) {
        this.amountCny = amountCny;
    }

    public BigDecimal getAmountCny() {
        return amountCny;
    }

    public void setPaidAmountCny(BigDecimal paidAmountCny) {
        this.paidAmountCny = paidAmountCny;
    }

    public BigDecimal getPaidAmountCny() {
        return paidAmountCny;
    }

    public void setTimeNum(Integer timeNum) {
        this.timeNum = timeNum;
    }

    public Integer getTimeNum() {
        return timeNum;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getGift() {
        return gift;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setIsShelf(Integer isShelf) {
        this.isShelf = isShelf;
    }

    public Integer getIsShelf() {
        return isShelf;
    }

    public void setIsExperience(Integer isExperience) {
        this.isExperience = isExperience;
    }

    public Integer getIsExperience() {
        return isExperience;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getMinCoverImage() {
        return minCoverImage;
    }

    public void setMinCoverImage(String minCoverImage) {
        this.minCoverImage = minCoverImage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("amountCny", getAmountCny())
                .append("paidAmountCny", getPaidAmountCny())
                .append("timeNum", getTimeNum())
                .append("gift", getGift())
                .append("courseName", getCourseName())
                .append("subject", getSubject())
                .append("color", getColor())
                .append("isShelf", getIsShelf())
                .append("isExperience", getIsExperience())
                .append("introduction", getIntroduction())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("minCoverImage", getMinCoverImage())
                .toString();
    }
}
