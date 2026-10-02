package com.zhenshu.jianxue.mec.educenter.domain.vo;

import java.util.List;
import java.util.Set;

/**
 * @author jing
 * @version 1.0
 * @desc 导出学员的时候用于承接参数的
 * @date 2021/2/3 0003 15:07
 **/
public class ExportStudentVO {

    /**
     * 电话号码集合
     */
    private Set<String> phoneSet;

    /**
     * 有效行数编号集合
     */
    private List<Integer> rowNumList;

    public Set<String> getPhoneSet() {
        return phoneSet;
    }

    public void setPhoneSet(Set<String> phoneSet) {
        this.phoneSet = phoneSet;
    }

    public List<Integer> getRowNumList() {
        return rowNumList;
    }

    public void setRowNumList(List<Integer> rowNumList) {
        this.rowNumList = rowNumList;
    }
}
