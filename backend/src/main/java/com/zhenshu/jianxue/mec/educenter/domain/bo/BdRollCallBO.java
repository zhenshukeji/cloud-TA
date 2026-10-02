package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 点名页面所返回的参数
 *
 * @author yuxi
 * @date 2020-10-13
 */
@ApiModel("点名页面所返回的参数")
public class BdRollCallBO extends OutputEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 班级信息
     */
    @ApiModelProperty("班级信息")
    private BdClassBO classBO;

    /**
     * 请假学生信息
     */
    @ApiModelProperty("请假学生信息")
    private List<BdAskLeaveBO> vacateStus;

    /**
     * 该班级的学生信息
     */
    @ApiModelProperty("该班级的学生信息")
    private List<BdCourseTimeBO> courseTimeList;

    /**
     * 该节课的上课时间开始
     */
    @ApiModelProperty("该节课的上课时间")
    private String startInWeek;

    /**
     * 该节课的上课时间结束
     */
    @ApiModelProperty("该节课的上课时间结束")
    private String endInWeek;

    public String getStartInWeek() {
        return startInWeek;
    }

    public void setStartInWeek(String startInWeek) {
        this.startInWeek = startInWeek;
    }

    public String getEndInWeek() {
        return endInWeek;
    }

    public void setEndInWeek(String endInWeek) {
        this.endInWeek = endInWeek;
    }

    public BdClassBO getClassBO() {
        return classBO;
    }

    public void setClassBO(BdClassBO classBO) {
        this.classBO = classBO;
    }

    public List<BdAskLeaveBO> getVacateStus() {
        return vacateStus;
    }

    public void setVacateStus(List<BdAskLeaveBO> vacateStus) {
        this.vacateStus = vacateStus;
    }

    public List<BdCourseTimeBO> getCourseTimeList() {
        return courseTimeList;
    }

    public void setCourseTimeList(List<BdCourseTimeBO> courseTimeList) {
        this.courseTimeList = courseTimeList;
    }

    @Override
    public String toString() {
        return "BdRollCallBO{" +
                "classBO=" + classBO +
                ", vacateStus=" + vacateStus +
                ", courseTimeList=" + courseTimeList +
                ", startInWeek='" + startInWeek + '\'' +
                ", endInWeek='" + endInWeek + '\'' +
                '}';
    }
}
