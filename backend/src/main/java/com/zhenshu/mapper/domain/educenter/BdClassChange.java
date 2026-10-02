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
 * 上课临时更改记录 对象 bd_class_change
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_class_change")
public class BdClassChange extends BaseEntity implements Serializable {
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
     * 班级id
     */
    private Long classId;

    /**
     * 更改类型0取消1新增2调课
     */
    private Integer type;

    /**
     * 班级上课时间id（调课）
     */
    private Long classTimeId;

    /**
     * 开始时间
     */
    private Date clsStartTime;

    /**
     * 结束时间
     */
    private Date clsEndTime;

    /**
     *  原本上课开始时间
     */
    private Date originalStartTime;

    /**
     *  原本上课结束时间
     */
    private Date originalEndTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 是否删除
     */
    @Version
    private Integer version;

    public Date getOriginalStartTime() {
        return originalStartTime;
    }

    public void setOriginalStartTime(Date originalStartTime) {
        this.originalStartTime = originalStartTime;
    }

    public Date getOriginalEndTime() {
        return originalEndTime;
    }

    public void setOriginalEndTime(Date originalEndTime) {
        this.originalEndTime = originalEndTime;
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

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setClassTimeId(Long classTimeId) {
        this.classTimeId = classTimeId;
    }

    public Long getClassTimeId() {
        return classTimeId;
    }

    public void setClsStartTime(Date clsStartTime) {
        this.clsStartTime = clsStartTime;
    }

    public Date getClsStartTime() {
        return clsStartTime;
    }

    public void setClsEndTime(Date clsEndTime) {
        this.clsEndTime = clsEndTime;
    }

    public Date getClsEndTime() {
        return clsEndTime;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("campusId", getCampusId())
                .append("classId", getClassId())
                .append("type", getType())
                .append("classTimeId", getClassTimeId())
                .append("clsStartTime", getClsStartTime())
                .append("clsEndTime", getClsEndTime())
                .append("isDelete", getIsDelete())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
