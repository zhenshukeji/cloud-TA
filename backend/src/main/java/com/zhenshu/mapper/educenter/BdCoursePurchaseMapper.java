package com.zhenshu.mapper.educenter;

import java.util.List;

import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCoursePurchaseBO;
import com.zhenshu.mapper.domain.educenter.BdCoursePurchase;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 课程购买记录 Mapper接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface BdCoursePurchaseMapper extends BaseMapper<BdCoursePurchase> {

    /**
     * 查询课程购买记录
     *
     * @param studentId 学生id
     * @return CoursePurchaseStu
     */
    List<BdCoursePurchaseBO> selectListByStudentId(Long studentId);

}
