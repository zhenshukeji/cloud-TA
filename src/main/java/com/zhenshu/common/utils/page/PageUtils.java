package com.zhenshu.common.utils.page;

import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;
/**
 * @author yuxi
 * @desc 封装类
 */
public class PageUtils<T> {

    public Page<T> getPage(List<T> list, Integer total){
        Page<T> resultPage = new Page<>();
        resultPage.setRecords(list);
        resultPage.setTotal(total);
        return resultPage;
    }
}
