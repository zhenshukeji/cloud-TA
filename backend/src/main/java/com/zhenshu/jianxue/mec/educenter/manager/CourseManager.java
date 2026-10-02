package com.zhenshu.jianxue.mec.educenter.manager;

import com.zhenshu.common.enums.educenter.ClassEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassService;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.StringJoiner;

/**
 * 课程调用其他表的service
 * @author  yuxi
 * @date 2020-10-20 13:58
 */
@Component
public class CourseManager {

    @Resource
    private IBdClassService classService;

    @Resource
    private IBdCourseService courseService;

    /**
     * 删除课程
     *
     * @param ids 课程id
     */
    public void deleteCourse(Long[] ids) {
        for (Long id : ids){
            BdCourseBO courseBO = courseService.selectBdCourseById(id);
            if (courseBO == null){
                throw new CustomException("未查询到该课程");
            }
            BdClassVO classVO = new BdClassVO();
            classVO.setClassCourseId(id);
            classVO.setClassState(ClassEnum.NORMAL_STATUS.getCode());
            List<BdClassBO> list = classService.getAll(classVO);
            if (list != null && list.size() > 0){
                StringJoiner joiner = new StringJoiner(",");
                for (BdClassBO classBO : list){
                    joiner.add(classBO.getClassName());
                }
                throw new CustomException("已有班级绑定的课程不可删除，班级为：".concat(joiner.toString()));
            }
            courseService.deleteBdCourseById(id);
        }
    }
}
