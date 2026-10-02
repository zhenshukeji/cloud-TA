package com.zhenshu.jianxue.mec.educenter.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdRenewalWarnBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeUpdateVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdRenewalWarnVO;
import com.zhenshu.mapper.domain.educenter.BdCourseTime;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentInfoBO;

import java.util.List;
import java.util.Map;

/**
 * 课次 Service接口
 *
 * @author jing
 * @date 2020-10-04
 */
public interface IBdCourseTimeService extends IService<BdCourseTime> {

    /**
     * 查询课次
     *
     * @param id 课次 ID
     * @return 课次
     */
    BdCourseTimeBO selectBdCourseTimeById(Long id);

    /**
     * 查询课次
     *
     * @param bdCourseTimeVO 课次 ID
     * @return 课次
     */
    BdCourseTimeBO selectBdCourseTimeByVO(BdCourseTimeVO bdCourseTimeVO);


    /**
     * 新增课次
     *
     * @param bdCourseTimeVO 课次
     * @return 结果
     */
    boolean insertBdCourseTime(BdCourseTimeVO bdCourseTimeVO);


    /**
     * 根据id进行课次更新
     *
     * @param courseTimeUpdateVO 课次修改
     * @return boolean
     */
    boolean updateCourseTimeById(BdCourseTimeUpdateVO courseTimeUpdateVO);

    /**
     * 修改总课次
     * @param courseTimeUpdateVO 修改课次信息
     */
    void updateTotalCourseTime(BdCourseTimeUpdateVO courseTimeUpdateVO);

    /**
     * 根据学生id，课程id进行扣课次处理 -- 由点名生成的
     *
     * @param bdCourseTimeVO 课次修改
     * @return boolean
     */
    Long minusOneCourseTime(BdCourseTimeVO bdCourseTimeVO);

    /**
     * 通过课程id与学生ID找到课次
     *
     * @param bdCourseTimeVO bdCourseTimeVO
     * @return BdCourseTimeBO
     */
    BdCourseTimeBO getBdCourseTimeBO(BdCourseTimeVO bdCourseTimeVO);

    /**
     * 根据课程购买记录更新还学员的课次数量
     *
     * @param bdCourseTimeVO 课次信息
     * @param num            增加的数量
     * @return 更新是否成功
     */
    Long updateByCoursePurchase(BdCourseTimeVO bdCourseTimeVO, Integer num);

    /**
     * 查询课次少于6课次的学生
     *
     * @param renewalWarnVO 学员 课程名称
     * @return 课次少于6课次的学生集合
     */
    List<BdRenewalWarnBO> getRenewalWarnList(BdRenewalWarnVO renewalWarnVO);

    /**
     * 根据学员id列表和课程id 查询课时信息
     *
     * @param studentIds    学员id 列表
     * @param courseId 课程id
     * @return <学员id , 课时信息>
     */
    Map<Long, BdCourseTimeBO> selectByStuIdsAndCourseId(Long[] studentIds, Long courseId);

     /**
     * 获取学员课次统计
     *
     * @param courseTimeVO 传参
     */
    List<StudentInfoBO> selectStudentInfo(BdCourseTimeVO courseTimeVO);



    /**
     * 原子退 1 课次（纠错改请假，OS022）
     *
     * @param id 课次id
     * @return 受影响行数
     */
    int refundOneCourseTime(Long id);

    /**
     * 原子重扣 1 课次（纠错改回其他状态，OS022）：仅当剩余 >= 1，不允许扣成负数
     *
     * @param id 课次id
     * @return 受影响行数（0 表示剩余课次不足）
     */
    int deductOneCourseTimeIfEnough(Long id);

    /**
     * 锁定读未删除的课时账户（FOR UPDATE，OS022）：并发首次报读兜底回查用，
     * 锁定读读的是最新已提交版本，不受 REPEATABLE READ 快照影响。
     *
     * @param studentId 学员id
     * @param courseId  课程id
     * @return 未删除账户行（已锁定该行直到事务结束）
     */
    BdCourseTime selectActiveAccountForUpdate(Long studentId, Long courseId);
}
