package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCoursePurchaseBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCoursePurchaseVO;
import com.zhenshu.mapper.domain.educenter.BdCoursePurchase;

import java.util.List;

/**
 * 课程购买记录 Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdCoursePurchaseService extends IService<BdCoursePurchase> {

    /**
     * 新增课程购买记录
     *
     * @param bdCoursePurchaseVO 课程购买记录
     * @return 结果
     */
    boolean insertBdCoursePurchase(BdCoursePurchaseVO bdCoursePurchaseVO);

    /**
     * 根据学生id 获取课程购买记录
     *
     * @param studentId 学生id
     * @return 课程购买记录
     */
    List<BdCoursePurchaseBO> selectListByStudentId(Long studentId);

}
