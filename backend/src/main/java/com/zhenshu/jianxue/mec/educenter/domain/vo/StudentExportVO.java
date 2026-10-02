package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.zhenshu.framework.aspectj.lang.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 学员课时账户导出行（OS024，PRD C14）。
 *
 * <p>一行 = 学员 × 课程的课时账户；无任何账户的学员输出一行，课程/课时/金额留空。
 * 单位口径：课时列明确「课时」，金额列明确「元·人民币」（amount_cny，方案 B）——
 * 同一文件不得出现第二套单位。xlsx 单元格一律为文本类型，名称类字段即使以
 * {@code = + - @} 开头也不会被 Excel 当作公式执行。
 */
public class StudentExportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Excel(name = "学员姓名")
    private String studentName;

    @Excel(name = "手机号")
    private String studentsPhone;

    @Excel(name = "家长姓名")
    private String parentsName;

    @Excel(name = "家长手机号")
    private String parentsPhone;

    @Excel(name = "班级")
    private String classNames;

    @Excel(name = "课程")
    private String courseName;

    @Excel(name = "总课时（课时）", cellType = Excel.ColumnType.NUMERIC)
    private Integer totalNumber;

    @Excel(name = "剩余课时（课时）", cellType = Excel.ColumnType.NUMERIC)
    private Integer remainingNumber;

    @Excel(name = "累计报读金额（元·人民币）", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal amountCnySum;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentsPhone() {
        return studentsPhone;
    }

    public void setStudentsPhone(String studentsPhone) {
        this.studentsPhone = studentsPhone;
    }

    public String getParentsName() {
        return parentsName;
    }

    public void setParentsName(String parentsName) {
        this.parentsName = parentsName;
    }

    public String getParentsPhone() {
        return parentsPhone;
    }

    public void setParentsPhone(String parentsPhone) {
        this.parentsPhone = parentsPhone;
    }

    public String getClassNames() {
        return classNames;
    }

    public void setClassNames(String classNames) {
        this.classNames = classNames;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getRemainingNumber() {
        return remainingNumber;
    }

    public void setRemainingNumber(Integer remainingNumber) {
        this.remainingNumber = remainingNumber;
    }

    public BigDecimal getAmountCnySum() {
        return amountCnySum;
    }

    public void setAmountCnySum(BigDecimal amountCnySum) {
        this.amountCnySum = amountCnySum;
    }
}
