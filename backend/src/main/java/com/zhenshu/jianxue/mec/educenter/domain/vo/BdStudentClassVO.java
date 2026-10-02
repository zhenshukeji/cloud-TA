package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 学生班级中间对象 bd_student_class
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("学生班级中间主体")
public class BdStudentClassVO extends InputEntity implements Serializable {
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
     *  学生id
     */
    @ApiModelProperty(value="学生id")
    private Long studentId;

    @ApiModelProperty(value = "学生ids", required = true)
    private List<Long> studentIds;

    /**
     *  班级id
     */
    @ApiModelProperty(value="班级id", required = true)
    private Long classId;

    /**
     *  添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("添加时间")
    private Date haveTime;

    /**
     *  0：没有删除1：已删除
     */
    @ApiModelProperty("0：没有删除1：已删除")
    private Integer isDelete;

    /**
     *  学生名称
     */
    @ApiModelProperty("学生名称")
    private String studentName;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setHaveTime(Date haveTime) {
        this.haveTime = haveTime;
    }

    public Date getHaveTime() {
        return haveTime;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
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
