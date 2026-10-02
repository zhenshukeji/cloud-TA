package com.zhenshu.mapper.domain.base;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.zhenshu.framework.web.domain.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 校区对象 bd_campus
 *
 * @author jing
 * @date 2020-10-04
 */
@TableName("bd_campus")
public class BdCampus extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分校区名称
     */
    private String name;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 维度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 所属机构
     */
    private Long mecId;


    /**
     * 省份
     */
    private String province;

    /**
     * 是否会员 0=普通会员，1=黄金会员
     */
    private Integer isMember;

    /**
     * 会员到期时间
     */
    private Date memberEndTime;

    /**
     * 城市
     */
    private String city;

    /**
     * 是否删除 0=未删除 1=已删除
     */
    private Integer isDelete;

    /**
     * 校区负责人登录账号
     */
    private String mgrPhone;

    /**
     * 校区负责人联系电话
     */
    private String mgrContactNumber;

     /**
     * 校区负责人名称
     */
    private String mgrName;

    public Date getMemberEndTime() {
        return memberEndTime;
    }

    public void setMemberEndTime(Date memberEndTime) {
        this.memberEndTime = memberEndTime;
    }

    public Integer getIsMember() {
        return isMember;
    }

    public void setIsMember(Integer isMember) {
        this.isMember = isMember;
    }

    public String getMgrPhone() {
        return mgrPhone;
    }

    public void setMgrPhone(String mgrPhone) {
        this.mgrPhone = mgrPhone;
    }

    public String getMgrName() {
        return mgrName;
    }

    public void setMgrName(String mgrName) {
        this.mgrName = mgrName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public Long getMecId() {
        return mecId;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getMgrContactNumber() {
        return mgrContactNumber;
    }

    public void setMgrContactNumber(String mgrContactNumber) {
        this.mgrContactNumber = mgrContactNumber;
    }

    @Override
    public String toString() {
        return "BdCampus{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", mecId=" + mecId +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", isDelete=" + isDelete +
                ", mgrPhone='" + mgrPhone + '\'' +
                ", mgrName='" + mgrName + '\'' +
                '}';
    }
}
