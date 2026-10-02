package com.zhenshu.mapper.domain.base;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.aspectj.lang.annotation.Excel;
import com.baomidou.mybatisplus.annotations.TableName;
import com.zhenshu.framework.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;


/**
 * 机构人员和校区的关联 对象 bd_campus_connection
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_campus_connection")
public class BdCampusConnection extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
        id
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
        机构id
    */
    private Long mecId;

    /**
        机构人员id
    */
    private Long mecPersonnelId;

    /**
        校区id
    */
    private Long campusId;


    /**
        是否删除
    */
    private Integer isDelete;


     /**
     * 校区名称
     */
    private String campusName;

    public String getCampusName() {
        return campusName;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMecId() {
        return mecId;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public Long getMecPersonnelId() {
        return mecPersonnelId;
    }

    public void setMecPersonnelId(Long mecPersonnelId) {
        this.mecPersonnelId = mecPersonnelId;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("mecPersonnelId", getMecPersonnelId())
                .append("campusId", getCampusId())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
