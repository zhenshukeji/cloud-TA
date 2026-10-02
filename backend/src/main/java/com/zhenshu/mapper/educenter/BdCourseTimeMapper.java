package com.zhenshu.mapper.educenter;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdRenewalWarnBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdRenewalWarnVO;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentInfoBO;
import com.zhenshu.mapper.domain.educenter.BdCourseTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课时 Mapper接口
 *
 * @author jing
 * @date 2020-09-25
 */
public interface BdCourseTimeMapper extends BaseMapper<BdCourseTime> {

    /**
     * 查询课次少于6课次的学生
     *
     * @param renewalWarnVO 学员 课程名称
     * @return 课次少于6课次的学生集合
     */
    List<BdRenewalWarnBO> getRenewalWarnList(BdRenewalWarnVO renewalWarnVO);

    /**
     * 获取学员课次统计
     *
     * @param courseTimeVO 传参
     * @return 续费预警
     */
    List<StudentInfoBO> selectStudentInfo(BdCourseTimeVO courseTimeVO);

    /**
     * 原子扣减课次：仅当剩余课次 >= 1 时才扣减，返回受影响行数
     *
     * @param id 课次id
     * @return 受影响行数（0 表示剩余课次不足）
     */
    int minusOneIfEnough(@Param("id") Long id);

    /**
     * 原子增课次（报读）：剩余与总课时同步加 num，不读后写，杜绝并发丢更新
     *
     * @param id     课次id
     * @param num    增加的课时数
     * @param userId 操作人
     * @return 受影响行数
     */
    int addCourseTime(@Param("id") Long id, @Param("num") Integer num, @Param("userId") String userId);

    /**
     * 原子增减课次（手动调整）：delta 为正加、为负减，不读后写
     *
     * @param id    课次id
     * @param delta 增减量（可正可负）
     * @return 受影响行数
     */
    int adjustCourseTime(@Param("id") Long id, @Param("delta") Integer delta);

    /**
     * 条件改总课次：仅当目标总课次 >= 当前剩余课次时生效（原子，替代读后写校验）
     *
     * @return 受影响行数（0 表示低于剩余课次）
     */
    int changeTotalCourseTimeIfEnough(@Param("id") Long id, @Param("num") Integer num);

    /**
     * 原子退 1 课次（纠错改请假）
     *
     * @return 受影响行数
     */
    int refundOneCourseTime(@Param("id") Long id);

    /**
     * 原子重扣 1 课次（纠错从请假改回其他状态）：仅当剩余 >= 1，不允许扣成负数
     *
     * @return 受影响行数（0 表示剩余课次不足）
     */
    int deductOneCourseTimeIfEnough(@Param("id") Long id);

    /**
     * 锁定读未删除的课时账户（FOR UPDATE，OS022）：
     * 并发首次报读撞唯一键后的兜底回查必须用锁定读——
     * REPEATABLE READ 的快照读看不到对方事务刚提交的行。
     *
     * @return 未删除账户行（已锁定该行直到事务结束）
     */
    BdCourseTime selectActiveAccountForUpdate(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}
