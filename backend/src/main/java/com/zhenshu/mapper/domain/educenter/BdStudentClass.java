package com.zhenshu.mapper.domain.educenter;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;
import com.zhenshu.framework.web.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;


/**
 * 学生班级中间对象 bd_student_class
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_student_class")
public class BdStudentClass extends BaseEntity implements Serializable {
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
     *  学生id
     */
    private Long studentId;

    /**
     *  班级id
     */
    private Long classId;

    /**
     *  添加时间
     */
    private Date haveTime;

    /**
     *  0：没有删除1：已删除
     */
    private Integer isDelete;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getUpdateBy() {
        return updateBy;
    }

    @Override
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

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
    public void setStudentId(Long studentId){
        this.studentId = studentId;
    }

    public Long getStudentId(){
        return studentId;
    }
    public void setClassId(Long classId){
        this.classId = classId;
    }

    public Long getClassId(){
        return classId;
    }
    public void setHaveTime(Date haveTime){
        this.haveTime = haveTime;
    }

    public Date getHaveTime(){
        return haveTime;
    }
    public void setIsDelete(Integer isDelete){
        this.isDelete = isDelete;
    }

    public Integer getIsDelete(){
        return isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
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
