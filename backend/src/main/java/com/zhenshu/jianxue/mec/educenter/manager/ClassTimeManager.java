package com.zhenshu.jianxue.mec.educenter.manager;

import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseBO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 课程表调用其他表的service
 *
 * @author zyx
 * @date 2020-10-07
 */
@Service
public class ClassTimeManager {

    @Resource
    private IBdCourseService courseService;

    @Resource
    private CommonManager commonManager;

    /**
     * 通过班级信息
     *
     * @param classId 班级id
     * @return Map<String, Object>
     */
    public Map<String, Object> getCls(Long classId) {
        BdClassBO classBO = commonManager.selectBdClassDetailedById(classId);
        Map<String, Object> map = new HashMap<>();
        map.put("helpTeacherName", classBO.getHelpTeacherName());
        map.put("className", classBO.getClassName());
        map.put("bishopTeacherName", classBO.getBishopTeacherName());
        map.put("sumStudents", classBO.getSumStudents());
        map.put("existStudents", classBO.getExistStudents());
        //得到课程名称
        Long courseId = classBO.getClassCourseId();
        BdCourseBO courseBO = courseService.selectBdCourseById(courseId);
        map.put("courseName", courseBO.getCourseName());
        return map;
    }



}
