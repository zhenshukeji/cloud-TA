package com.zhenshu.jianxue.mec.educenter.manager;

import com.zhenshu.common.enums.educenter.CallRecordEnum;
import com.zhenshu.common.enums.educenter.CourseTimeEnum;
import com.zhenshu.common.enums.educenter.CourseTimeUpEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.OwnershipGuard;
import com.zhenshu.common.manager.mec.educenter.ClassRecordWithSysUserManager;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCallRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassRecordBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCallRecordVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCallRecordService;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassRecordService;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseTimeService;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseTimeUpdateService;
import com.zhenshu.mapper.domain.educenter.BdCallRecord;
import com.zhenshu.mapper.domain.educenter.BdClassRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuxi
 * @version 1.0
 * @desc 点名记录调用其他表
 * @date 2020-10-13 14:22
 */
@Service
public class ClassRecordManager {

    @Resource
    private IBdCallRecordService callRecordService;

    @Resource
    private IBdClassRecordService classRecordService;

    @Resource
    private ClassRecordWithSysUserManager classRecordWithSysUserManager;

    @Resource
    private IBdCourseTimeUpdateService courseTimeUpdateService;

    @Resource
    private IBdCourseTimeService courseTimeService;


    /**
     * 获得上课记录的详细信息
     *
     * @param classRecordId 上课记录id
     * @return 上课记录详细信息
     */
    public BdClassRecordBO selectBdClassRecordDetailById(Long classRecordId) {
        BdClassRecordBO bdClassRecordBO = classRecordService.selectBdClassRecordDetailById(classRecordId);
        // 1.获得点名记录学生详情
        List<BdCallRecordBO> list = callRecordService.getListByClassRecordId(classRecordId);
        bdClassRecordBO.setCallRecordList(list);
        // 5.点名老师
        String userName = classRecordWithSysUserManager.getCallTeacherName(Long.valueOf(bdClassRecordBO.getCreateBy()));
        bdClassRecordBO.setCallTeacher(userName);
        return bdClassRecordBO;
    }

    /**
     * 获取该学员的点名信息
     *
     * @param callRecordId 消课记录id
     * @return 消课信息
     */
    public BdCallRecordBO getCallRecordInfo(Long callRecordId) {
        return callRecordService.selectDetailById(callRecordId);
    }

    /**
     * 保存该学员的点名信息
     *
     * <p>并发口径（OS022）：事务开头先以 {@code FOR UPDATE} 锁定本条课消记录，
     * 并发纠错（含两次相同纠错）在此串行化；课时的退/扣改为原子 SQL，
     * 不再「读余额 → 计算后整体覆盖」。整体不变量：剩余课时 == 期初 + 流水净值。
     *
     * @param callRecordVO 该学员的点名信息
     * @return 是否保存成功
     */
    @Transactional(rollbackFor=Exception.class)
    public Boolean saveRollCall(BdCallRecordVO callRecordVO) {
        // 锁定本条课消记录：同一记录的并发纠错在此排队，读到的状态是已提交的最终状态
        BdCallRecord locked = callRecordService.selectByIdForUpdate(callRecordVO.getId());
        if (locked == null){
            throw new CustomException(String.format("消课记录id为%s不存在", callRecordVO.getId()));
        }
        // OS023 归属守卫：课消记录必须属于当前会话机构/校区
        OwnershipGuard.checkOwned(locked.getMecId(), locked.getCampusId());
        // 判断更新学员的状态是否为请假，如果为请假的话，则需要退换课次
        BdCallRecordBO callRecord = callRecordService.selectDetailById(callRecordVO.getId());
        if (callRecord == null){
            throw new CustomException(String.format("消课记录id为%s不存在", callRecordVO.getId()));
        }
        if(callRecordVO.getType() == null){
            throw new CustomException("请填写状态");
        }
        if (callRecordVO.getType().equals(callRecord.getType())){
            throw new CustomException("状态相同无需修改");
        }
        BdCourseTimeVO courseTimeVO = new BdCourseTimeVO();
        courseTimeVO.setStudentId(callRecord.getStudentId());
        courseTimeVO.setCourseId(callRecord.getCourseId());
        BdCourseTimeBO courseTimeBO = courseTimeService.getBdCourseTimeBO(courseTimeVO);
        // 课次记录缺失时给出明确业务提示，避免空指针导致退/补课次逻辑无法执行
        if (courseTimeBO == null) {
            throw new CustomException("该学员暂无该课程的课次记录");
        }
        // 如果修改状态为请假，但是之前的状态不为请假，则需要将学生的课次进行添加
        if (callRecordVO.getType().equals(CallRecordEnum.LEAVE_STATE.getCode()) && !callRecord.getType().equals(CallRecordEnum.LEAVE_STATE.getCode())){
            // 退回 1 课时（原子增）
            changeCourseTime(courseTimeBO, CourseTimeUpEnum.ADD_OPR, CourseTimeUpEnum.CHANGE_TO_LEAVE);
            callRecordVO.setClassTimeNum(CourseTimeEnum.ZERO_COURSE_TIME.getCode());
            // 如果之前的状态为请假，现在修改为其他的状态，则需要将扣课次
        } else if (callRecord.getType().equals(CallRecordEnum.LEAVE_STATE.getCode()) && !callRecordVO.getType().equals(CallRecordEnum.LEAVE_STATE.getCode())){
            // 重新扣 1 课时（原子减，剩余不足即失败回滚，不扣成负数）
            changeCourseTime(courseTimeBO, CourseTimeUpEnum.REDUCE_OPR, CourseTimeUpEnum.CHANGE_TO_OTHER_TYPE);
            callRecordVO.setClassTimeNum(CourseTimeEnum.ONE_COURSE_TIME.getCode());
        }
        // 更新学员的点名信息
        callRecordService.updateBdCallRecord(callRecordVO);
        // 查询出该上课记录的到课人数
        Integer num = callRecordService.selectNumOfAttendByClassRecordId(callRecord.getClassRecordId(), CallRecordEnum.TO_CLASS.getCode());
        // 更新上课记录实到人数
        BdClassRecord classRecord = new BdClassRecord();
        classRecord.setId(callRecord.getClassRecordId());
        classRecord.setRealNumber(num);
        return classRecordService.updateById(classRecord);
    }


    /**
     * 退/扣 1 课时并记录流水。
     *
     * <p>OS022：退/扣均为单条原子 SQL；重扣方向仅在剩余课时 >= 1 时生效，
     * 否则抛出异常令整个纠错事务回滚。
     */
    public void changeCourseTime(BdCourseTimeBO courseTimeBO, CourseTimeUpEnum operation, CourseTimeUpEnum changeToOtherType) {
        if (CourseTimeUpEnum.ADD_OPR == operation) {
            courseTimeService.refundOneCourseTime(courseTimeBO.getId());
        } else {
            int rows = courseTimeService.deductOneCourseTimeIfEnough(courseTimeBO.getId());
            if (rows == 0) {
                throw new CustomException("该学员剩余课次不足，无法重新扣课");
            }
        }
        // 增加课次修改记录
        courseTimeUpdateService.insertUpdateRecord(courseTimeBO, operation, changeToOtherType);
    }
}
