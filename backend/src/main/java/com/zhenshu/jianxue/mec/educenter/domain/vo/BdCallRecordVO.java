package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 课消记录对象 bd_call_record
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("课消记录主体")
public class BdCallRecordVO extends InputEntity implements Serializable {
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
     *  班级id
     */
    @ApiModelProperty(value="班级id", required = true)
    private Long classId;

    /**
     *  学生id
     */
    @ApiModelProperty(value="学生id", required = true)
    private Long studentId;

    /**
     *  上课记录id
     */
    @ApiModelProperty("上课记录id")
    private Long classRecordId;

    /**
     *  消课课时
     */
    @ApiModelProperty("消课课时")
    private Integer classTimeNum;

    /**
     *  0:到课 1:迟到 2：请假 3:未到4提前请假
     */
    @ApiModelProperty(value="0:到课 1:迟到 2：请假 3:未到4提前请假", required = true)
    private Integer type;

    /**
     *  是否删除
     */
    @ApiModelProperty(value="是否删除", required = true)
    private Integer isDelete;

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

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setClassRecordId(Long classRecordId) {
        this.classRecordId = classRecordId;
    }

    public Long getClassRecordId() {
        return classRecordId;
    }

    public void setClassTimeNum(Integer classTimeNum) {
        this.classTimeNum = classTimeNum;
    }

    public Integer getClassTimeNum() {
        return classTimeNum;
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
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("classId", getClassId())
                .append("studentId", getStudentId())
                .append("classRecordId", getClassRecordId())
                .append("classTimeNum", getClassTimeNum())
                .append("type", getType())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
