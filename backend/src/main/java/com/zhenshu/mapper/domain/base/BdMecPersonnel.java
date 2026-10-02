package com.zhenshu.mapper.domain.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;


/**
 * 机构人员 对象 bd_mec_personnel
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_mec_personnel")
public class BdMecPersonnel extends BaseEntity implements Serializable {
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
     * 姓名
     */
    private String name;

    /**
     * 登录账号（之前是手机号，但是因为客户觉得手机号不好迁移，也不好随便新增对应的登录账号，为了使得账号可以继承，所以从手机号改成登录账号。不再校验只是手机号）
     */
    private String phone;

    /**
     * 手机联系号码
     */
    private String contactNumber;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 账号类型 0机构管理员 1机构人员
     */
    private Integer type;

    /**
     * 与校区有关的表
     */
    @TableField(exist = false)
    private List<BdCampusConnection> campusConnectionList;

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

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<BdCampusConnection> getCampusConnectionList() {
        return campusConnectionList;
    }

    public void setCampusConnectionList(List<BdCampusConnection> campusConnectionList) {
        this.campusConnectionList = campusConnectionList;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("mecId", getMecId())
                .append("name", getName())
                .append("phone", getPhone())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("isDelete", getIsDelete())
                .toString();
    }
}
