package com.zhenshu.mapper.domain.educenter;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;


/**
 * 学生对象 bd_student
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_student")
public class BdStudent extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
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
     * 学生姓名
     */
    private String studentName;

    /**
     * 学生手机号码
     */
    private String studentsPhone;

    /**
     * 学校/幼儿园名称
     */
    private String schoolName;

    /**
     * 年级
     */
    private String grade;

    /**
     * 年龄
     */
    private String age;

    /**
     * 地址
     */
    private String address;

    /**
     * 关系
     * 0=父亲 1=母亲 2=爷爷 3=奶奶 4=外公 5=外婆 6=其他
     */
    private Integer associated;

    /**
     * 家长姓名
     */
    private String parentsName;

    /**
     * 家长手机号码
     */
    private String parentsPhone;

    /**
     *  性别
     *  0=男 1=女 2=未知
     */
    private Integer sex;

    /**
     * 报名时间
     */
    private Date registerTime;

    /**
     * 是否删除 0=未删除 1=删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 0：正常学员1：回收站学员
     */
    private Integer isOrdinary;


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

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentsPhone(String studentsPhone) {
        this.studentsPhone = studentsPhone;
    }

    public String getStudentsPhone() {
        return studentsPhone;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAge() {
        return age;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAssociated(Integer associated) {
        this.associated = associated;
    }

    public Integer getAssociated() {
        return associated;
    }

    public void setParentsName(String parentsName) {
        this.parentsName = parentsName;
    }

    public String getParentsName() {
        return parentsName;
    }

    public void setParentsPhone(String parentsPhone) {
        this.parentsPhone = parentsPhone;
    }

    public String getParentsPhone() {
        return parentsPhone;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsOrdinary(Integer isOrdinary) {
        this.isOrdinary = isOrdinary;
    }

    public Integer getIsOrdinary() {
        return isOrdinary;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("studentName", getStudentName())
                .append("studentsPhone", getStudentsPhone())
                .append("schoolName", getSchoolName())
                .append("grade", getGrade())
                .append("age", getAge())
                .append("address", getAddress())
                .append("associated", getAssociated())
                .append("parentsName", getParentsName())
                .append("parentsPhone", getParentsPhone())
                .append("sex", getSex())
                .append("registerTime", getRegisterTime())
                .append("isDelete", getIsDelete())
                .append("isOrdinary", getIsOrdinary())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
