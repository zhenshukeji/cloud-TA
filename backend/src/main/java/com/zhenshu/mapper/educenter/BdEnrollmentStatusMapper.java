package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.EnrollmentStatusBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.EnrollmentStatusVO;

import java.util.List;

/**
 * @author jing
 * @desc 报读情况
 * @date
 */
public interface BdEnrollmentStatusMapper extends BaseMapper<EnrollmentStatusVO> {

    /**
     * 报读情况
     * @param enrollmentVO 学员名称 手机号 报读课程 所在班级
     * @return 报读情况
     */
    List<EnrollmentStatusBO> enrollmentList(EnrollmentStatusVO enrollmentVO);
}
