package com.zhenshu.framework.web.page;

import com.zhenshu.common.constant.HttpStatus;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表格分页数据对象
 *
 * @author zhenshu
 */
public class TableDataInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    @ApiModelProperty("总记录数")
    private long total;

    /** 列表数据 */
    @ApiModelProperty("列表数据")
    private List<?> rows;

    /** 消息状态码 */
    @ApiModelProperty("消息状态码")
    private int code;

    /** 消息内容 */
    @ApiModelProperty("消息内容")
    private String msg;

    /**
     * 表格数据对象
     */
    public TableDataInfo()
    {
        this.code = HttpStatus.SUCCESS;
        this.rows = new ArrayList<>();
    }

    /**
     * 分页
     *
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<?> list, int total)
    {
        this.rows = list;
        this.total = total;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<?> getRows()
    {
        return rows;
    }

    public void setRows(List<?> rows)
    {
        this.rows = rows;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }
}
