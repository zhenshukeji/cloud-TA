package com.zhenshu.framework.web.domain;

import com.zhenshu.common.constant.HttpStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Result")
public class Result<T> {
    /**
     * 1.status状态值：代表本次请求response的状态结果。
     */
    @ApiModelProperty("状态码")
    private Integer code;
    /**
     * 2.response描述：对本次状态码的描述。
     */
    @ApiModelProperty("描述")
    private String msg;
    /**
     * 3.data数据：本次返回的数据。
     */
    @ApiModelProperty("数据")
    private T data;

    /**
     * 成功，创建ResResult：有data数据
     */
    public Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 成功，创建ResResult：有data数据
     */
    public Result<T> success() {
        Result<T> result = new Result<T>();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    /**
     * 失败，指定status、desc
     */
    public Result<T> fail(Integer code, String desc) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(desc);
        return result;
    }

    /**
     * 失败，指定status、desc
     */
    public Result<T> fail(String desc) {
        Result<T> result = new Result<T>();
        result.setCode(HttpStatus.ERROR);
        result.setMsg(desc);
        return result;
    }

    /**
     * 失败，指定ResultCode枚举
     */
    public Result<T> fail(ResultCode resultCode) {
        Result<T> result = new Result<T>();
        result.setResultCode(resultCode);
        return result;
    }

    /**
     * 把ResultCode枚举转换为ResResult
     */
    private void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

