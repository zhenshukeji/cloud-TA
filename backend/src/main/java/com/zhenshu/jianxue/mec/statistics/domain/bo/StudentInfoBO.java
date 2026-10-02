package com.zhenshu.jianxue.mec.statistics.domain.bo;

/**
 * @author jing
 * @version 1.0
 * @desc 学生信息
 * @date 2020/11/13 0013 10:00
 **/
public class StudentInfoBO {

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 课程名称
     */
    private String courseNames;

    /**
     * 班级名称
     */
    private String classNames;


    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseNames() {
        return courseNames;
    }

    public void setCourseNames(String courseNames) {
        this.courseNames = courseNames;
    }

    public String getClassNames() {
        return classNames;
    }

    public void setClassNames(String classNames) {
        this.classNames = classNames;
    }
}
