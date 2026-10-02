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
 * 老师对象 bd_teacher
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("老师主体")
public class BdTeacherBO extends OutputEntity implements Serializable {
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
     *  老师名称
     */
    @ApiModelProperty("老师名称")
    private String name;

    /**
     *  手机号码 手机号码
     */
    @ApiModelProperty("手机号码")
    private String phone;

    /**
     *  0:男  1：女
     */
    @ApiModelProperty("0:男  1：女")
    private String sex;

    /**
     *  入职时间 入职时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("入职时间 入职时间")
    private Date inductionTime;

    /**
     *  离职时间 离职时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("离职时间 离职时间")
    private Date departureTime;

    /**
     *  已授班级数
     */
    @ApiModelProperty("已授班级数")
    private Integer taughtNum;

    /**
     *  0:未离职  1：已离职 0:未离职  1：已离职
     */
    @ApiModelProperty("0:未离职  1：已离职 0:未离职  1：已离职")
    private Integer isDeparture;

    /**
     *  0:未删除1：已删除 0:未删除  1：已删除
     */
    @ApiModelProperty("0:未删除1：已删除 0:未删除  1：已删除")
    private Integer isDelete;

    /**
     * 主教班级
     */
    @ApiModelProperty("主教班级")
    private String bishopClasses;

    /**
     * 助教班级
     */
    @ApiModelProperty("助教班级")
    private String assistantClasses;

    /**
     *  0=机构人员 1=校区人员
     */
    private Integer personType;

    /**
     *  关联的人员id
     */
    private Long associationPerson;

    /**
     *  关联的人员姓名
     */
    @ApiModelProperty("关联的人员姓名")
    private String associationPersonName;

    /**
     *  已授班级人数
     */
    @ApiModelProperty("已授班级人数")
    private Integer taughtStuNum;

    public Integer getPersonType() {
        return personType;
    }

    public void setPersonType(Integer personType) {
        this.personType = personType;
    }

    public Long getAssociationPerson() {
        return associationPerson;
    }

    public void setAssociationPerson(Long associationPerson) {
        this.associationPerson = associationPerson;
    }

    public String getAssociationPersonName() {
        return associationPersonName;
    }

    public void setAssociationPersonName(String associationPersonName) {
        this.associationPersonName = associationPersonName;
    }

    public Integer getTaughtStuNum() {
        return taughtStuNum;
    }

    public void setTaughtStuNum(Integer taughtStuNum) {
        this.taughtStuNum = taughtStuNum;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }

    public void setInductionTime(Date inductionTime) {
        this.inductionTime = inductionTime;
    }

    public Date getInductionTime() {
        return inductionTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setTaughtNum(Integer taughtNum) {
        this.taughtNum = taughtNum;
    }

    public Integer getTaughtNum() {
        return taughtNum;
    }

    public void setIsDeparture(Integer isDeparture) {
        this.isDeparture = isDeparture;
    }

    public Integer getIsDeparture() {
        return isDeparture;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public String getBishopClasses() {
        return bishopClasses;
    }

    public void setBishopClasses(String bishopClasses) {
        this.bishopClasses = bishopClasses;
    }

    public String getAssistantClasses() {
        return assistantClasses;
    }

    public void setAssistantClasses(String assistantClasses) {
        this.assistantClasses = assistantClasses;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("name", getName())
                .append("phone", getPhone())
                .append("sex", getSex())
                .append("inductionTime", getInductionTime())
                .append("departureTime", getDepartureTime())
                .append("taughtNum", getTaughtNum())
                .append("isDeparture", getIsDeparture())
                .append("bishopClasses", getBishopClasses())
                .append("assistantClasses", getAssistantClasses())
                .append("isDelete", getIsDelete())
                .append("taughtStuNum", getTaughtStuNum())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
