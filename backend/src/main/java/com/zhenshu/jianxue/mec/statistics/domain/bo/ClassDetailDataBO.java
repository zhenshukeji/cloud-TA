package com.zhenshu.jianxue.mec.statistics.domain.bo;

import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc 班级明细报表
 * @date 2020/11/12 0012 16:45
 **/
public class ClassDetailDataBO {

      /**
     *  id
     */
    private Long id;

    /*
     *  主教老师名称
     */
    private String bishopTeacherName;

    /**
     *  主教老师
     */
    private Long bishopTeacherId;


    /**
     *  容量
     */
    private Integer sumStudents;

    /**
     *  已有学员
     */
    private Integer existStudents;

    /**
     *  班级名称
     */
    private String className;

    /**
     *  上课时间
     */
    private List<String> classTimes;


    /**
     * 未到人数
     */
    private Integer notArrived;

    /**
     * 请假人数
     */
    private Integer leaveNum;

    /**
     * 迟到人数
     */
    private Integer lateNum;

    /**
     * 实到人数
     */
    private Integer actuallyNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBishopTeacherName() {
        return bishopTeacherName;
    }

    public void setBishopTeacherName(String bishopTeacherName) {
        this.bishopTeacherName = bishopTeacherName;
    }

    public Long getBishopTeacherId() {
        return bishopTeacherId;
    }

    public void setBishopTeacherId(Long bishopTeacherId) {
        this.bishopTeacherId = bishopTeacherId;
    }

    public Integer getSumStudents() {
        return sumStudents;
    }

    public void setSumStudents(Integer sumStudents) {
        this.sumStudents = sumStudents;
    }

    public Integer getExistStudents() {
        return existStudents;
    }

    public void setExistStudents(Integer existStudents) {
        this.existStudents = existStudents;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getClassTimes() {
        return classTimes;
    }

    public void setClassTimes(List<String> classTimes) {
        this.classTimes = classTimes;
    }

    public Integer getNotArrived() {
        return notArrived;
    }

    public void setNotArrived(Integer notArrived) {
        this.notArrived = notArrived;
    }

    public Integer getLeaveNum() {
        return leaveNum;
    }

    public void setLeaveNum(Integer leaveNum) {
        this.leaveNum = leaveNum;
    }

    public Integer getLateNum() {
        return lateNum;
    }

    public void setLateNum(Integer lateNum) {
        this.lateNum = lateNum;
    }

    public Integer getActuallyNum() {
        return actuallyNum;
    }

    public void setActuallyNum(Integer actuallyNum) {
        this.actuallyNum = actuallyNum;
    }

    @Override
    public String toString() {
        return "ClassDetailDataBo{" +
                "id=" + id +
                ", bishopTeacherName='" + bishopTeacherName + '\'' +
                ", bishopTeacherId=" + bishopTeacherId +
                ", sumStudents=" + sumStudents +
                ", existStudents=" + existStudents +
                ", className='" + className + '\'' +
                ", classTimes=" + classTimes +
                ", notArrived=" + notArrived +
                ", leaveNum=" + leaveNum +
                ", lateNum=" + lateNum +
                ", actuallyNum=" + actuallyNum +
                '}';
    }
}
