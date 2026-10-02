package com.zhenshu.common.manager.mec.statistics;

import com.zhenshu.jianxue.mec.statistics.domain.bo.ClassDetailDataBO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassService;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassTimeService;
import com.zhenshu.jianxue.mec.statistics.domain.vo.BdClassDetailVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author jing
 * @version 1.0
 * @desc 班级明细报表
 * @date 2020/11/12 0012 16:37
 **/
@Service
public class ClassDetailManager {

    @Resource
    private IBdClassService bdClassService;

    @Resource
    private IBdClassTimeService classTimeService;

    public List<ClassDetailDataBO> getClassData(BdClassDetailVO bdClassDetailVO) {
        List<ClassDetailDataBO> list = bdClassService.getClassData(bdClassDetailVO);
        getClassListInfo(list);
        return list;
    }

    /**
     * 获取班级详细信息，包括上课时间
     *
     * @param classList 班级列表
     */
    public void getClassListInfo(List<ClassDetailDataBO> classList) {
        // 获取班级id 列表
        Long[] classIds = new Long[classList.size()];
        int num = 0;
        for (ClassDetailDataBO classBO : classList) {
            classIds[num] = classBO.getId();
            num += 1;
        }
        // 获取班级相关的上课时间
        Map<Long, List<String>> classTimeMap = classTimeService.selectListByClassIds(classIds);
        for (ClassDetailDataBO classBO : classList) {
            // 根据班级id获取班级上课时间
            List<String> classTimeList = classTimeMap.get(classBO.getId());
            classBO.setClassTimes(classTimeList);
        }
    }

}
