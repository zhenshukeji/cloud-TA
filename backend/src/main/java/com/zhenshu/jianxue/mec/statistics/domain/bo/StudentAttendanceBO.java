package com.zhenshu.jianxue.mec.statistics.domain.bo;

/**
 * @author jing
 * @version 1.0
 * @desc 学员考勤统计
 * @date 2020/11/12 0012 17:20
 **/
public class StudentAttendanceBO {

    /**
     * 课程名称
     */
    private String courseName;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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
}
