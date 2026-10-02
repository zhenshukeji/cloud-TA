package com.zhenshu.mapper.domain.educenter;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.Version;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;
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
@TableName("bd_teacher")
public class BdTeacher extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *  id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     *  机构id
     */
    private Long mecId;

    /**
     *  校区id
     */
    private Long campusId;

    /**
     *  老师名称 老师名称
     */
    private String name;

    /**
     *  手机号码 手机号码
     */
    private String phone;

    /**
     *  0:男  1：女
     */
    private String sex;

    /**
     *  入职时间 入职时间
     */
    private Date inductionTime;

    /**
     *  离职时间 离职时间
     */
    private Date departureTime;

    /**
     *  已授班级数
     */
    private Integer taughtNum;

    /**
     *  0:未离职  1：已离职 0:未离职  1：已离职
     */
    private Integer isDeparture;

    /**
     *  0:未删除1：已删除 0:未删除  1：已删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     *  0=机构人员 1=校区人员
     */
    private Integer personType;

    /**
     *  关联的人员id
     */
    private Long associationPerson;

    /**
     * 乐观锁
     */
    @Version
    private Integer version;

    public void setId(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }
    public void setMecId(Long mecId){
        this.mecId = mecId;
    }

    public Long getMecId(){
        return mecId;
    }
    public void setCampusId(Long campusId){
        this.campusId = campusId;
    }

    public Long getCampusId(){
        return campusId;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhone(){
        return phone;
    }
    public void setSex(String sex){
        this.sex = sex;
    }

    public String getSex(){
        return sex;
    }
    public void setInductionTime(Date inductionTime){
        this.inductionTime = inductionTime;
    }

    public Date getInductionTime(){
        return inductionTime;
    }
    public void setDepartureTime(Date departureTime){
        this.departureTime = departureTime;
    }

    public Date getDepartureTime(){
        return departureTime;
    }
    public void setTaughtNum(Integer taughtNum){
        this.taughtNum = taughtNum;
    }

    public Integer getTaughtNum(){
        return taughtNum;
    }
    public void setIsDeparture(Integer isDeparture){
        this.isDeparture = isDeparture;
    }

    public Integer getIsDeparture(){
        return isDeparture;
    }
    public void setIsDelete(Integer isDelete){
        this.isDelete = isDelete;
    }

    public Integer getIsDelete(){
        return isDelete;
    }

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("personType", getPersonType())
                .append("campusPerson", getAssociationPerson())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
