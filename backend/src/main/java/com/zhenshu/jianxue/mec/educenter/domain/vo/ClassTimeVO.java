package com.zhenshu.jianxue.mec.educenter.domain.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 课程表入参VO
 * 由于课程表管理与班级上课时间VO冲突
 *
 * @author jing
 * @date 2020-10-04
 */
public class ClassTimeVO implements Serializable {

    /**
     * 班级名称
     */
    @ApiModelProperty(value = "班级名称")
    private String className;

    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;

    /**
     * 那一周 实例：0=当前周 1=下周 -1=上周
     */
    @ApiModelProperty(value = "那一周 实例：0=当前周 1=下周 -1=上周", required = true)
    private Integer num = 0;

    /**
     * id
     */
    @ApiModelProperty(value = "id", required = true)
    private Long id;

    /**
     * 班级id
     */
    @ApiModelProperty(value = "班级id")
    private Long classId;

    /**
     * id类型 0=班级上课时间表id 1=调课记录id
     */
    @ApiModelProperty(value = "id类型")
    private Long idType;

    /**
     * 机构id
     */
    @ApiModelProperty(value = "机构id")
    private Long mecId;

    /**
     * 校区id
     */
    @ApiModelProperty(value = "校区id")
    private Long campusId;

    /**
     * 哪一天
     */
    @ApiModelProperty(value = "哪一天")
    private Integer weekday;

    /**
     * 星期列表
     */
    private List<Integer> weekDayList;

    public List<Integer> getWeekDayList() {
        return weekDayList;
    }

    public void setWeekDayList(List<Integer> weekDayList) {
        this.weekDayList = weekDayList;
    }

    public Long getIdType() {
        return idType;
    }

    public void setIdType(Long idType) {
        this.idType = idType;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getMecId() {
        return mecId;
    }

    public void setMecId(Long mecId) {
        this.mecId = mecId;
    }

    public Long getCampusId() {
        return campusId;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    @Override
    public String toString() {
        return "ClassTimeVO{" +
                "className='" + className + '\'' +
                ", courseName='" + courseName + '\'' +
                ", num=" + num +
                ", id=" + id +
                ", classId=" + classId +
                ", idType=" + idType +
                ", mecId=" + mecId +
                ", campusId=" + campusId +
                ", weekday=" + weekday +
                '}';
    }
}
