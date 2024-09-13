package com.zhenshu.cloud.platform.statistics.domain.vo;

import java.io.Serializable;

/**
 * 平台流水数据导出封装
 */
public class PlatformPipelineExportVO implements Serializable {

    /**
     * 年份
     */
    private  String years;

    /**
     * 月份
     */
    private String months;

    /**
     * 季度 1,2,3,4
     */
    private Integer quarterly;

    public String getYears() {
        return years;
    }

    public void setYears(String years) {
        this.years = years;
    }

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public Integer getQuarterly() {
        return quarterly;
    }

    public void setQuarterly(Integer quarterly) {
        this.quarterly = quarterly;
    }

}
