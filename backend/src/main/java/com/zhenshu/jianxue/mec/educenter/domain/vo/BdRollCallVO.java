package com.zhenshu.jianxue.mec.educenter.domain.vo;

import com.zhenshu.framework.web.domain.InputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 点名入参VO
 */
@ApiModel("点名入参VO")
public class BdRollCallVO extends InputEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 班级信息
     */
    @ApiModelProperty("班级信息")
    private BdClassVO classVO;

    /**
     * 请假学生信息
     */
    @ApiModelProperty("请假学生信息")
    private List<BdAskLeaveVO> vacateStus;

    /**
     * 该班级的学生信息
     */
    @ApiModelProperty("该班级的学生信息")
    private List<BdCallRecordVO> callRecordList;

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

    public BdClassVO getClassVO() {
        return classVO;
    }

    public void setClassVO(BdClassVO classVO) {
        this.classVO = classVO;
    }

    public List<BdAskLeaveVO> getVacateStus() {
        return vacateStus;
    }

    public void setVacateStus(List<BdAskLeaveVO> vacateStus) {
        this.vacateStus = vacateStus;
    }

    public List<BdCallRecordVO> getCallRecordList() {
        return callRecordList;
    }

    public void setCallRecordList(List<BdCallRecordVO> callRecordList) {
        this.callRecordList = callRecordList;
    }

    @Override
    public String toString() {
        return "BdRollCallVO{" +
                "classVO=" + classVO +
                ", vacateStus=" + vacateStus +
                ", callRecordList=" + callRecordList +
                ", startInWeek='" + startInWeek + '\'' +
                ", endInWeek='" + endInWeek + '\'' +
                '}';
    }
}
