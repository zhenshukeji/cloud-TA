package com.zhenshu.jianxue.mec.educenter.domain.bo;

import com.zhenshu.framework.web.domain.OutputEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("学生详细信息中课时修改信息")
public class UpCouInfoStuBO extends OutputEntity {
    private static final long serialVersionUID = 1L;
    /**
     * 课程名称
     */
    @ApiModelProperty("课程名称")
    private String courseName;

    /**
     * 操作原因
     */
    @ApiModelProperty("操作原因")
    private String reason;

    /**
     * 操作
     */
    @ApiModelProperty("操作")
    private String operation;

    /**
     * 创建人
     */
    @ApiModelProperty("操作人")
    private String operationName;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }
}
