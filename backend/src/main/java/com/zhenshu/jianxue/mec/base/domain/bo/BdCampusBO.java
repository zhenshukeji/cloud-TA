package com.zhenshu.jianxue.mec.base.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 校区对象 bd_campus
 *
 * @author jing
 * @date 2020-10-04
 */
@ApiModel("校区主体")
public class BdCampusBO extends OutputEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Long id;
    /**
     * 分校区名称
     */
    @ApiModelProperty("分校区名称")
    private String name;
    /**
     * 详细地址
     */
    @ApiModelProperty("详细地址")
    private String address;
    /**
     * 维度
     */
    @ApiModelProperty("维度")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @ApiModelProperty("经度")
    private BigDecimal longitude;

    /**
     * 所属机构
     */
    @ApiModelProperty("所属机构")
    private Long mecId;

    /**
     * 机构名称
     */
    @ApiModelProperty("机构名称")
    private String mecName;

    /**
     * 省份
     */
    @ApiModelProperty("省份")
    private String province;
    /**
     * 城市
     */
    @ApiModelProperty("城市")
    private String city;
    /**
     * 是否删除 0=未删除 1=已删除
     */
    @ApiModelProperty("是否删除 0=未删除 1=已删除")
    private Integer isDelete;

    /**
     * 校区负责人登录账号
     */
    @ApiModelProperty("校区负责人登录账号")
    private String mgrPhone;

    /**
     * 校区负责人联系电话
     */
    @ApiModelProperty("校区负责人联系电话")
    private String mgrContactNumber;

    /**
     * 校区负责人名称
     */
    @ApiModelProperty("校区负责人名称")
    private String mgrName;

    /**
     * 班级数
     */
    @ApiModelProperty("班级数")
    private Integer classCount;

    /**
     * 学生数
     */
    @ApiModelProperty("学生数")
    private Integer studentCount;

    /**
     * 老师数
     */
    @ApiModelProperty("老师数")
    private Integer teacherCount;

    /**
     * 课程数
     */
    @ApiModelProperty("课程数")
    private Integer courseCount;

    /**
     * 是否会员 0=不是会员，1=会员
     */
    private Integer isMember;

    /**
     * 会员到期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date memberEndTime;

    @ApiModelProperty("创建人名称")
    private String createByName;

    @ApiModelProperty("当前登录者名称")
    private String currentLoginName;

    public String getCurrentLoginName() {
        return currentLoginName;
    }

    public void setCurrentLoginName(String currentLoginName) {
        this.currentLoginName = currentLoginName;
    }


    public String getMecName() {
        return mecName;
    }

    public void setMecName(String mecName) {
        this.mecName = mecName;
    }

    public String getCreateByName() {
        return createByName;
    }

    public void setCreateByName(String createByName) {
        this.createByName = createByName;
    }

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

    public String getMgrContactNumber() {
        return mgrContactNumber;
    }

    public void setMgrContactNumber(String mgrContactNumber) {
        this.mgrContactNumber = mgrContactNumber;
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

    public Integer getClassCount() {
        return classCount;
    }

    public void setClassCount(Integer classCount) {
        this.classCount = classCount;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getTeacherCount() {
        return teacherCount;
    }

    public void setTeacherCount(Integer teacherCount) {
        this.teacherCount = teacherCount;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }

    @Override
    public String toString() {
        return "BdCampusBO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", mecId=" + mecId +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", isDelete=" + isDelete +
                ", mgrPhone='" + mgrPhone + '\'' +
                ", mgrName='" + mgrName + '\'' +
                ", classCount=" + classCount +
                ", studentCount=" + studentCount +
                ", teacherCount=" + teacherCount +
                ", courseCount=" + courseCount +
                '}';
    }
}
