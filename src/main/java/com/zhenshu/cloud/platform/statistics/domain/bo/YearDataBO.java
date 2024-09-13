package com.zhenshu.cloud.platform.statistics.domain.bo;

/**
 * @author yuheng
 * @version 1.0
 * @date 2020/12/2 10:21
 * @desc 年份数据返回包装
 */
public class YearDataBO {

    /**
     * 年
     */
    private String yearNum;

    /**
     * 数量
     */
    private Integer num;

    public String getYearNum() {
        return yearNum;
    }

    public void setYearNum(String yearNum) {
        this.yearNum = yearNum;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
