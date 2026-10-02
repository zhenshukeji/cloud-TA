package com.zhenshu.mapper.educenter;

import com.zhenshu.jianxue.mec.educenter.domain.bo.UpCouInfoStuBO;
import com.zhenshu.mapper.domain.educenter.BdCourseTimeUpdate;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * 课时修改记录 Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdCourseTimeUpdateMapper extends BaseMapper<BdCourseTimeUpdate> {


    /**
     * 获取学员课次修改类型为手工操作的纪录
     * @param studentId 学员id
     * @return 学员课次修改类型为手工操作的纪录
     */
    List<UpCouInfoStuBO> selectListByStudentId(Long studentId);
}
