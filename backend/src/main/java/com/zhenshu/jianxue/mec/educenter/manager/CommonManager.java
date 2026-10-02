package com.zhenshu.jianxue.mec.educenter.manager;

import com.zhenshu.common.exception.CustomException;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassTimeBO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassService;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassTimeService;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseService;
import com.zhenshu.jianxue.mec.educenter.service.IBdTeacherService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/21 0021 17:55
 **/
@Service
public class CommonManager {

    private final static Log log = LogFactory.getLog(CommonManager.class);

    @Resource
    private IBdCourseService bdCourseService;

    @Resource
    private IBdTeacherService teacherService;

    @Resource
    private IBdClassTimeService classTimeService;

    @Resource
    private IBdClassService classService;

    /**
     * 查询班级详细信息，包括上课时间表，课程名称，主教老师名称，辅助老师名称
     *
     * @param id 班级ID
     * @return 班级信息
     */
    public BdClassBO selectBdClassDetailedById(Long id) {
        BdClassBO bdClassBO = classService.selectBaseInfoById(id);
        if (bdClassBO == null) {
            log.warn(String.format("查询班级详细信息时出现根据id没有查到数据的情况，班级id是 %s ", id));
            throw new CustomException("没有找到对应班级的信息");
        }
        // 获取该班级的上课时间表
        List<BdClassTimeBO> classTimeList = classTimeService.getClassTimeByClassId(bdClassBO.getId());

        // 根据星期顺序排列
        Comparator<BdClassTimeBO> weekDayComparator = new Comparator<BdClassTimeBO>() {
            @Override
            public int compare(BdClassTimeBO o1, BdClassTimeBO o2) {
                return o1.getWeekDay() - o2.getWeekDay();
            }
        };
        classTimeList.sort(weekDayComparator);

        bdClassBO.setClassTimeList(classTimeList);
        // 获得课程名称
        String courseName = bdCourseService.selectBdCourseById(bdClassBO.getClassCourseId()).getCourseName();
        bdClassBO.setCourseName(courseName);
        // 获得主教老师
        String bishopTeacherName = teacherService.selectBdTeacherById(bdClassBO.getBishopTeacherId()).getName();
        bdClassBO.setBishopTeacherName(bishopTeacherName);
        // 获取辅助老师名称
        Long helpTeacherId = bdClassBO.getHelpTeacherId();
        if (helpTeacherId != null){
            String helpTeacherName = teacherService.selectBdTeacherById(helpTeacherId).getName();
            bdClassBO.setHelpTeacherName(helpTeacherName);
        }
        return bdClassBO;
    }


}
