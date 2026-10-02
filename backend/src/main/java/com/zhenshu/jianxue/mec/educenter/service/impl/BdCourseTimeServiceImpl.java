package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.CourseTimeEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdRenewalWarnBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeUpdateVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdRenewalWarnVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseTimeService;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentInfoBO;
import com.zhenshu.mapper.domain.educenter.BdCourseTime;
import com.zhenshu.mapper.educenter.BdCourseTimeMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课时 Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdCourseTimeServiceImpl extends ServiceImpl<BdCourseTimeMapper, BdCourseTime> implements IBdCourseTimeService {

    @Resource
    BdCourseTimeMapper courseTimeMapper;

    /**
     * 查询课时
     *
     * @param id 课时 ID
     * @return 课时
     */
    @Override
    public BdCourseTimeBO selectBdCourseTimeById(Long id) {
        EntityWrapper<BdCourseTime> wrapper = new EntityWrapper<>();
        wrapper.eq("id", id);
        BdCourseTime bdCourseTime = this.selectOne(wrapper);
        if (bdCourseTime == null) {
            return null;
        }
        BdCourseTimeBO bdCourseTimeBO = new BdCourseTimeBO();
        BeanUtils.copyBeanProp(bdCourseTimeBO, bdCourseTime);
        return bdCourseTimeBO;
    }

    /**
     * 查询课时
     *
     * @param bdCourseTimeVO 课时 ID
     * @return 课时
     */
    @Override
    public BdCourseTimeBO selectBdCourseTimeByVO(BdCourseTimeVO bdCourseTimeVO) {
        BdCourseTime param = voToPo(bdCourseTimeVO);
        EntityWrapper<BdCourseTime> wrapper = new EntityWrapper<>();
        wrapper.setEntity(param);
        BdCourseTime result = this.selectOne(wrapper);
        if (result == null) {
            return null;
        }
        BdCourseTimeBO bdCourseTimeBO = new BdCourseTimeBO();
        BeanUtils.copyBeanProp(bdCourseTimeBO, result);
        return bdCourseTimeBO;
    }


    /**
     * 新增课时
     *
     * @param bdCourseTimeVO 课时
     * @return 结果
     */
    @Override
    public boolean insertBdCourseTime(BdCourseTimeVO bdCourseTimeVO) {
        BdCourseTime bdCourseTime = voToPo(bdCourseTimeVO);
        bdCourseTime.setCreateTime(DateUtils.getNowDate());
        bdCourseTime.setCreateBy(SecurityUtils.getUserId().toString());
        bdCourseTime.setVersion(Constants.VERSION);
        boolean result = this.insert(bdCourseTime);
        bdCourseTimeVO.setId(bdCourseTime.getId());
        return result;
    }


    /**
     * 得到全部
     *
     * @param bdCourseTimeVO 课时信息
     * @return List<BdCourseTimeBO>
     */
    @Override
    public BdCourseTimeBO getBdCourseTimeBO(BdCourseTimeVO bdCourseTimeVO) {
        EntityWrapper<BdCourseTime> wrapper = new EntityWrapper<>();
        wrapper.setEntity(voToPo(bdCourseTimeVO));
        BdCourseTime bdCourseTime = this.selectOne(wrapper);
        if (bdCourseTime == null) {
            return null;
        }
        BdCourseTimeBO bdCourseTimeBO = new BdCourseTimeBO();
        BeanUtils.copyBeanProp(bdCourseTimeBO, bdCourseTime);
        return bdCourseTimeBO;
    }

    /**
     * 根据课程购买记录更新还学员的课时数量
     *
     * <p>并发口径（OS022）：已存在账户走<b>原子增</b>（不读后写）；账户不存在走插入，
     * 若并发首报撞 {@code uk_bd_course_time_active_account} 唯一键，则回退为对另一事务
     * 刚建好的账户做原子增，保证「两笔并发报读净增 = 两笔之和」且账户只有一份。
     *
     * @param bdCourseTimeVO 课时信息
     * @param num            增加的数量
     * @return 更新是否成功
     */
    @Override
    public Long updateByCoursePurchase(BdCourseTimeVO bdCourseTimeVO, Integer num) {
        EntityWrapper<BdCourseTime> wrapper = new EntityWrapper<>();
        wrapper.eq("student_id", bdCourseTimeVO.getStudentId());
        wrapper.eq("course_id", bdCourseTimeVO.getCourseId());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        // 1.先判断该学生是否拥有该门课程的课时记录
        List<BdCourseTime> list = courseTimeMapper.selectList(wrapper);
        if (!list.isEmpty()) {
            BdCourseTime courseTime = list.get(0);
            courseTimeMapper.addCourseTime(courseTime.getId(), num, SecurityUtils.getUserId().toString());
            return courseTime.getId();
        }
        BdCourseTime courseTime = voToPo(bdCourseTimeVO);
        courseTime.setStudentId(bdCourseTimeVO.getStudentId());
        courseTime.setCourseId(bdCourseTimeVO.getCourseId());
        courseTime.setTotalNumber(num);
        courseTime.setRemainingNumber(num);
        courseTime.setCreateTime(DateUtils.getNowDate());
        courseTime.setVersion(Constants.VERSION);
        courseTime.setCreateBy(SecurityUtils.getUserId().toString());
        try {
            this.insert(courseTime);
            return courseTime.getId();
        } catch (DuplicateKeyException e) {
            // 并发首次报读：另一事务已创建账户（唯一键 uk_bd_course_time_active_account 拦截），退化为原子增。
            // 兜底回查必须用锁定读：REPEATABLE READ 快照读看不到对方事务刚提交的行（OS022 实测踩坑）。
            BdCourseTime winner = courseTimeMapper.selectActiveAccountForUpdate(bdCourseTimeVO.getStudentId(), bdCourseTimeVO.getCourseId());
            if (winner == null) {
                throw new CustomException("该学员暂无该课程的课次记录，且创建失败，请重试");
            }
            courseTimeMapper.addCourseTime(winner.getId(), num, SecurityUtils.getUserId().toString());
            return winner.getId();
        }
    }

    /**
     * 查询课次少于6课次的学生
     *
     * @param renewalWarnVO 学员 课程名称
     * @return 课次少于6课次的学生集合
     */
    @Override
    public List<BdRenewalWarnBO> getRenewalWarnList(BdRenewalWarnVO renewalWarnVO) {
        return courseTimeMapper.getRenewalWarnList(renewalWarnVO);
    }

    /**
     * 根据学员id列表和课程id 查询课时信息
     *
     * @param studentIds 学员id 列表
     * @param courseId   课程id
     * @return <学员id , 课时信息>
     */
    @Override
    public Map<Long, BdCourseTimeBO> selectByStuIdsAndCourseId(Long[] studentIds, Long courseId) {
        EntityWrapper<BdCourseTime> wrapper = new EntityWrapper<>();
        wrapper.in("student_id", studentIds);
        wrapper.eq("course_id", courseId);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        // 查询数据
        List<BdCourseTime> courseTimeList = this.selectList(wrapper);
        // 根据学员id进行分隔
        HashMap<Long, BdCourseTimeBO> courseTimeMap = new HashMap<>();
        for (BdCourseTime courseTime : courseTimeList) {
            BdCourseTimeBO courseTimeBO = new BdCourseTimeBO();
            BeanUtils.copyBeanProp(courseTimeBO, courseTime);
            courseTimeMap.put(courseTime.getStudentId(), courseTimeBO);
        }
        return courseTimeMap;
    }

    /**
     * 获取学员课次统计
     *
     * @param courseTimeVO 传参
     */
    @Override
    public List<StudentInfoBO> selectStudentInfo(BdCourseTimeVO courseTimeVO) {
        SysUser sysUser = SecurityUtils.getSysUser();
        courseTimeVO.setMecId(sysUser.getMecId());
        courseTimeVO.setCampusId(sysUser.getCampusId());
        return courseTimeMapper.selectStudentInfo(courseTimeVO);
    }

    /**
     * 根据id进行课时更新
     *
     * <p>并发口径（OS022）：手动增减改为单条原子 SQL（delta = ±num），
     * 替代原「读出余额计算后整体覆盖」——后者在并发下会丢更新。
     *
     * @param courseTimeUpdateVO 课时修改
     * @return boolean
     */
    @Override
    public boolean updateCourseTimeById(BdCourseTimeUpdateVO courseTimeUpdateVO) {
        int delta;
        if (CourseTimeEnum.CHANGE_ADD.getCode() == courseTimeUpdateVO.getChangeType()) {
            delta = courseTimeUpdateVO.getNum();
        } else if (CourseTimeEnum.CHANGE_REDUCE.getCode() == courseTimeUpdateVO.getChangeType()) {
            // 调减课次时同步减少总课次，保持总课次与剩余课次口径一致
            delta = -courseTimeUpdateVO.getNum();
        } else {
            throw new CustomException("不支持的课时调整类型");
        }
        int rows = courseTimeMapper.adjustCourseTime(courseTimeUpdateVO.getCourseTimeId(), delta);
        return rows > 0;
    }

    /**
     * 修改总课次
     *
     * <p>并发口径（OS022）：校验与写入合并为一条条件原子 SQL
     * （仅当目标总课次 >= 当前剩余课次时生效），替代原「读出剩余课次先校验再覆盖」。
     *
     * @param courseTimeUpdateVO 修改课次信息
     */
    @Override
    public void updateTotalCourseTime(BdCourseTimeUpdateVO courseTimeUpdateVO) {
        int rows = courseTimeMapper.changeTotalCourseTimeIfEnough(courseTimeUpdateVO.getCourseTimeId(), courseTimeUpdateVO.getNum());
        if (rows == 0) {
            throw new CustomException("修改总课次不可低于剩余课次");
        }
    }

    /**
     * 根据学生id，课程id进行扣课时处理
     *
     * @param bdCourseTimeVO 课时修改
     * @return boolean
     */
    @Override
    public Long minusOneCourseTime(BdCourseTimeVO bdCourseTimeVO) {
        //1.根据学生id与课程id查找到课时信息
        BdCourseTime param = voToPo(bdCourseTimeVO);
        param.setStudentId(bdCourseTimeVO.getStudentId());
        param.setCourseId(bdCourseTimeVO.getCourseId());

        BdCourseTime result = courseTimeMapper.selectOne(param);
        // 课次记录缺失时给出明确业务提示，避免空指针导致点名事务整体回滚
        if (result == null) {
            throw new CustomException("该学员暂无该课程的课次记录");
        }
        // 原子扣减：仅当剩余课次 >= 1 才扣减，避免并发重复扣减与出现负课次
        int rows = courseTimeMapper.minusOneIfEnough(result.getId());
        if (rows == 0) {
            throw new CustomException("该学员剩余课次不足，无法扣减");
        }

        return result.getId();
    }

    /**
     * 原子退 1 课次（纠错改请假，OS022）
     */
    @Override
    public int refundOneCourseTime(Long id) {
        return courseTimeMapper.refundOneCourseTime(id);
    }

    /**
     * 原子重扣 1 课次（纠错改回其他状态，OS022）：仅当剩余 >= 1，不允许扣成负数
     */
    @Override
    public int deductOneCourseTimeIfEnough(Long id) {
        return courseTimeMapper.deductOneCourseTimeIfEnough(id);
    }

    /**
     * 锁定读未删除的课时账户（FOR UPDATE，OS022）
     */
    @Override
    public BdCourseTime selectActiveAccountForUpdate(Long studentId, Long courseId) {
        return courseTimeMapper.selectActiveAccountForUpdate(studentId, courseId);
    }

    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdCourseTimeVO 入参VO
     * @return bdCallRecord
     */
    public BdCourseTime voToPo(BdCourseTimeVO bdCourseTimeVO) {
        BdCourseTime bdCourseTime = new BdCourseTime();
        BeanUtils.copyBeanProp(bdCourseTime, bdCourseTimeVO);
        SysUser user = SecurityUtils.getSysUser();
        bdCourseTime.setCampusId(user.getCampusId());
        bdCourseTime.setMecId(user.getMecId());
        bdCourseTime.setIsDelete(Constants.NOT_DELETED);
        return bdCourseTime;
    }
}
