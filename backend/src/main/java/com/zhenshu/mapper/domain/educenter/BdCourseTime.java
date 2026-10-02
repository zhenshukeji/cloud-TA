package com.zhenshu.mapper.domain.educenter;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.annotation.Version;

import java.io.Serializable;


/**
 * 课时 对象 bd_course_time
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_course_time")
public class BdCourseTime extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 机构id
     */
    private Long mecId;

    /**
     * 校区id
     */
    private Long campusId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 总课时
     */
    private Integer totalNumber;

    /**
     * 剩余课时
     */
    private Integer remainingNumber;

    /**
     * 0:未删除1：已删除
     */
    private Integer isDelete;

    @Version
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getStudentId() {
        return studentId;
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

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setRemainingNumber(Integer remainingNumber) {
        this.remainingNumber = remainingNumber;
    }

    public Integer getRemainingNumber() {
        return remainingNumber;
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
                .append("studentId", getStudentId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("courseId", getCourseId())
                .append("totalNumber", getTotalNumber())
                .append("remainingNumber", getRemainingNumber())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("createBy", getCreateBy())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
