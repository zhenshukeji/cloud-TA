package com.zhenshu.cloud.platform.statistics.domain.bo;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/12/2 10:21
 * @desc
 */
public class DayDataBO {

    /**
     * 日期
     */
    private String date;

    /**
     * 数量
     */
    private Integer num;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "DayDataBO{" +
                "date='" + date + '\'' +
                ", num=" + num +
                '}';
    }
}
