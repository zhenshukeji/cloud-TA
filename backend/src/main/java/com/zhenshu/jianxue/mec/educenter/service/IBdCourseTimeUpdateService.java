package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.common.enums.educenter.CourseTimeUpEnum;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.UpCouInfoStuBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeUpdateVO;
import com.zhenshu.mapper.domain.educenter.BdCourseTimeUpdate;

import java.util.List;

/**
 * 课时修改记录 Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdCourseTimeUpdateService extends IService<BdCourseTimeUpdate> {

    /**
     * 分页查询课时修改记录 列表
     * @param studentId 学生id
     * @return 分页课时修改记录
     */
    List<UpCouInfoStuBO> selectBdCourseTimeUpdateList(Long studentId);

    /**
     * 新增课时修改记录
     *
     * @param bdCourseTimeUpdateVO 课时修改记录
     * @return 结果
     */
    boolean insertBdCourseTimeUpdate(BdCourseTimeUpdateVO bdCourseTimeUpdateVO);

    /**
     * 保存点名记录的修改课次记录
     * @param courseTimeBO
     */
    void insertUpdateRecord(BdCourseTimeBO courseTimeBO, CourseTimeUpEnum operation, CourseTimeUpEnum changeToOtherType);
}
