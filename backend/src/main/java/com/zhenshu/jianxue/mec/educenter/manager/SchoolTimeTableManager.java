package com.zhenshu.jianxue.mec.educenter.manager;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.CallRecordEnum;
import com.zhenshu.common.enums.educenter.CourseTimeUpEnum;
import com.zhenshu.common.enums.educenter.SchoolTableTimeEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.OwnershipGuard;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.*;
import com.zhenshu.jianxue.mec.educenter.domain.vo.*;
import com.zhenshu.jianxue.mec.educenter.service.*;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.mapper.domain.educenter.BdClassChange;
import com.zhenshu.mapper.domain.educenter.BdClassRecord;
import org.apache.poi.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author yuxi
 * @version 1.0
 * @desc 课表信息
 * @date 2020-10-15 14:35
 */
@Service
public class SchoolTimeTableManager {

    @Resource
    private IBdClassChangeService classChangeService;
    @Resource
    private IBdClassService classService;
    @Resource
    private IBdAskLeaveService askLeaveService;
    @Resource
    private IBdStudentService studentService;
    @Resource
    private IBdCourseTimeService courseTimeService;
    @Resource
    private IBdStudentClassService studentClassService;
    @Resource
    private IBdCourseTimeUpdateService courseTimeUpdateService;
    @Resource
    private IBdCallRecordService callRecordService;
    @Resource
    private IBdClassRecordService classRecordService;
    @Resource
    private IBdCourseService courseService;
    @Resource
    private IBdTeacherService teacherService;
    @Resource
    private TimeTableDataManager timeTableDataManager;


    //  课表信息中的课次修改接口

    /**
     * 课表信息中的课次修改
     *
     * @param bdClassTimeVO 课表信息中的课次
     * @return Boolean
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean changeClassTime(BdClassTimeVO bdClassTimeVO) {
        long currentTimeMillis = System.currentTimeMillis();
        long time = DateUtils.parseDate(bdClassTimeVO.getClsStartTime()).getTime();
        if (time < currentTimeMillis) {
            throw new CustomException("请调整大于当前时间的课");
        }
        boolean flag;
        if (timeTableDataManager.checkTime(bdClassTimeVO)) {
            throw new CustomException("该班级该时间段有课，请重新选择时间");
        }
        //如果idType为0，说明是班级上课时间表进行调课
        if (SchoolTableTimeEnum.CLASSTIME_TYPE.getCode() == bdClassTimeVO.getIdType()) {
            //说明是需要调课
            BdClassChangeVO bdClassChangeVO = classTimeVoToClassChangeVo(bdClassTimeVO);
            bdClassChangeVO.setType(SchoolTableTimeEnum.ADJUST_LESSON.getCode());
            flag = classChangeService.insertBdClassChange(bdClassChangeVO);
            //如果idType为1，说明是调课记录进行二次调课
        } else if (SchoolTableTimeEnum.CHANGE_TYPE.getCode() == bdClassTimeVO.getIdType()) {
            //修改课次
            BdClassChange classChange = classChangeService.selectById(bdClassTimeVO.getId());
            BdClassChangeVO bdClassChangeVO = new BdClassChangeVO();
            bdClassChangeVO.setId(bdClassTimeVO.getId());
            bdClassChangeVO.setClsStartTime(DateUtils.parseDate(bdClassTimeVO.getClsStartTime()));
            bdClassChangeVO.setClsEndTime(DateUtils.parseDate(bdClassTimeVO.getClsEndTime()));
            bdClassChangeVO.setOriginalStartTime(classChange.getOriginalStartTime());
            bdClassChangeVO.setOriginalEndTime(classChange.getOriginalEndTime());
            flag = classChangeService.updateBdClassChange(bdClassChangeVO);
        } else {
            throw new CustomException("调课失败");
        }
        return flag;
    }


    // 删除课表信息中的课次接口

    /**
     * 删除课表信息中的课次
     *
     * @param bdClassTimeVO 课表信息中的课次
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteClassTime(BdClassTimeVO bdClassTimeVO) {
        // 如果这个调课记录是调课或者新增课次
        if (SchoolTableTimeEnum.CHANGE_TYPE.getCode() == bdClassTimeVO.getIdType()) {
            // 删除调课表里的（调课或者新增课次）记录
            BdClassChange data = classChangeService.selectById(bdClassTimeVO.getId());

            BdClassChangeVO bdClassChangeVO = new BdClassChangeVO();
            bdClassChangeVO.setId(bdClassTimeVO.getId());
            bdClassChangeVO.setClassTimeId(data.getClassTimeId());
            bdClassChangeVO.setOriginalStartTime(DateUtils.parseDate(bdClassTimeVO.getStartInWeek()));
            bdClassChangeVO.setOriginalEndTime(DateUtils.parseDate(bdClassTimeVO.getClsEndTime()));
            bdClassChangeVO.setType(SchoolTableTimeEnum.CANCEL_LESSON.getCode());
            classChangeService.updateBdClassChange(bdClassChangeVO);
        } else if (SchoolTableTimeEnum.CLASSTIME_TYPE.getCode() == bdClassTimeVO.getIdType()) {
            // 新增一条取消课次的记录  初始化调课记录
            BdClassChangeVO bdClassChangeVO = new BdClassChangeVO();
            bdClassChangeVO.setType(SchoolTableTimeEnum.CANCEL_LESSON.getCode());
            bdClassChangeVO.setOriginalStartTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", bdClassTimeVO.getStartInWeek() + ":00"));
            bdClassChangeVO.setOriginalEndTime(DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", bdClassTimeVO.getEndInWeek() + ":00"));
            bdClassChangeVO.setClassId(bdClassTimeVO.getClassId());
            bdClassChangeVO.setClassTimeId(bdClassTimeVO.getId());
            bdClassChangeVO.setCampusId(SecurityUtils.getLoginCampusId());
            bdClassChangeVO.setMecId(SecurityUtils.getLoginMecId());
            bdClassChangeVO.setIsDelete(Constants.NOT_DELETED);
            classChangeService.insertBdClassChange(bdClassChangeVO);
        } else {
            throw new CustomException("传来的类型参数错误，没有定义该类型");
        }
    }

    /**
     * 班级上课信息转换为调课信息
     *
     * @param bdClassTimeVO 班级上课信息
     * @return BdClassChangeVO
     */
    public BdClassChangeVO classTimeVoToClassChangeVo(BdClassTimeVO bdClassTimeVO) {
        BdClassChangeVO bdClassChangeVO = new BdClassChangeVO();
        bdClassChangeVO.setClassTimeId(bdClassTimeVO.getId());
        bdClassChangeVO.setClassId(bdClassTimeVO.getClassId());
        bdClassChangeVO.setOriginalStartTime(bdClassTimeVO.getOriginalStartTime());
        bdClassChangeVO.setOriginalEndTime(bdClassTimeVO.getOriginalEndTime());
        bdClassChangeVO.setClsStartTime(DateUtils.parseDate(bdClassTimeVO.getClsStartTime()));
        bdClassChangeVO.setClsEndTime(DateUtils.parseDate(bdClassTimeVO.getClsEndTime()));
        return bdClassChangeVO;
    }

    // 进入点名页面接口

    /**
     * 进入点名页面
     *
     * @param bdClassTimeVO 班级上课
     * @return BdRollCallBO
     */
    public BdRollCallBO intoRollCall(BdClassTimeVO bdClassTimeVO) {
        Long classId = bdClassTimeVO.getClassId();
        Date startTime = parseFullTime(bdClassTimeVO.getStartInWeek());
        Date endTime = parseFullTime(bdClassTimeVO.getEndInWeek());
        // 校验不可重复点名
        checkWhetherTheRollCall(classId, startTime, endTime);
        //班级信息
        BdClassBO classBO = getClassInfo(bdClassTimeVO);
        //学员信息 1.请假人员
        List<BdAskLeaveBO> studentNames = getAskLeaveStuNames(bdClassTimeVO);
        //学员的姓名、总课时、剩余课时
        List<BdCourseTimeBO> courseTimeList = getStuCourseTimeList(bdClassTimeVO);
        BdRollCallBO rollCallBO = new BdRollCallBO();
        rollCallBO.setClassBO(classBO);
        rollCallBO.setCourseTimeList(courseTimeList);
        rollCallBO.setVacateStus(studentNames);
        rollCallBO.setStartInWeek(bdClassTimeVO.getStartInWeek());
        rollCallBO.setEndInWeek(bdClassTimeVO.getEndInWeek());
        return rollCallBO;
    }


    /**
     * 获得在该段时间请假的学生
     *
     * @param bdClassTimeVO 班级上课
     * @return List<String>
     */
    public List<BdAskLeaveBO> getAskLeaveStuNames(BdClassTimeVO bdClassTimeVO) {
        //1.这节课开始的上课时间 这节课结束的上课时间
        String endInWeek = bdClassTimeVO.getEndInWeek();
        String startWeek = bdClassTimeVO.getStartInWeek();
        //2.使用当前时间去请假表中找出在这段时间请假的学生
        List<BdAskLeaveBO> list = askLeaveService.selectStudentAtTime(endInWeek, startWeek);
        if (list.size() <= 0) {
            return null;
        }
        //3.将找出的学生看是否在这个班级中
        List<BdStudentClassBO> stuClsList = getStudentByClassId(bdClassTimeVO);
        Map<Long, Object> stuClsMap = new HashMap<>();
        for (BdStudentClassBO studentClassBO : stuClsList) {
            stuClsMap.put(studentClassBO.getStudentId(), studentClassBO);
        }
        //4.如果在这个班级中，则是属于提前请假的状态
        list.removeIf(askLeaveBO -> !stuClsMap.containsKey(askLeaveBO.getStudentId()));
        return list;
    }

    /**
     * 得到该班级的学员
     *
     * @param bdClassTimeVO 班级上课入参VO
     * @return List<BdStudentClassBO>
     */
    public List<BdStudentClassBO> getStudentByClassId(BdClassTimeVO bdClassTimeVO) {
        BdStudentClassVO vo = new BdStudentClassVO();
        vo.setClassId(bdClassTimeVO.getClassId());
        vo.setStudentName(bdClassTimeVO.getStudentName());
        return studentClassService.getAll(vo);
    }


    /**
     * 通过班级上课获得班级信息
     *
     * @param bdClassTimeVO 班级上课
     * @return BdClassBO
     */
    public BdClassBO getClassInfo(BdClassTimeVO bdClassTimeVO) {
        Long classId = bdClassTimeVO.getClassId();
        BdClassBO classBO = classService.selectBaseInfoById(classId);
        // 获得主教老师与辅助老师
        if (classBO.getHelpTeacherId() != null) {
            BdTeacherBO helpTeacher = teacherService.selectBdTeacherById(classBO.getHelpTeacherId());
            classBO.setHelpTeacherName(helpTeacher.getName());
        }
        BdTeacherBO bishopTeacher = teacherService.selectBdTeacherById(classBO.getBishopTeacherId());
        classBO.setBishopTeacherName(bishopTeacher.getName());
        // 课程名称
        BdCourseBO courseBO = courseService.selectBdCourseById(classBO.getClassCourseId());
        classBO.setCourseName(courseBO.getCourseName());
        return classBO;
    }


    // 点名接口

    /**
     * 点名
     *
     * @param rollCallVO 上课记录入参VO
     */
    @Transactional(rollbackFor = Exception.class)
    public void rollCall(BdRollCallVO rollCallVO) {
        Long classId = rollCallVO.getClassVO().getId();
        String startInWeek = toFullTimeStr(rollCallVO.getStartInWeek());
        Date startTime = parseFullTime(rollCallVO.getStartInWeek());
        String endInWeek = toFullTimeStr(rollCallVO.getEndInWeek());
        Date endTime = parseFullTime(rollCallVO.getEndInWeek());
        checkWhetherTheRollCall(classId, startTime, endTime);
        String classTime = DateUtils.getClassTime(startInWeek, endInWeek);
        //1.生成上课记录
        // 获取班级详细信息
        BdClassBO classBO = classService.selectClassDetailById(classId);
        // OS023 归属守卫：目标班级必须属于当前会话机构/校区，防止跨机构点名消耗他人课时
        OwnershipGuard.checkOwned(classBO.getMecId(), classBO.getCampusId());
        // 初始化填写上课记录
        BdClassRecord bdClassRecord = addClassRecord(startTime, endTime, classBO);
        //2.生成学生的消课记录
        Integer num = generateClassRecord(rollCallVO, bdClassRecord, classBO, classTime);
        //3.更新上课记录的实到人数
        bdClassRecord.setRealNumber(num);
        classRecordService.updateById(bdClassRecord);
        //4.更新班级的已授课次
        classService.updateClassHasClass(bdClassRecord);
    }

    /**
     * 校验不可重复点名
     *
     * @param classId   班级id
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    private void checkWhetherTheRollCall(Long classId, Date startTime, Date endTime) {
        BdClassRecord checkRollCall = new BdClassRecord();
        checkRollCall.setClassId(classId);
        checkRollCall.setClassStartTime(startTime);
        checkRollCall.setClassEndTime(endTime);
        if (classRecordService.checkRollCall(checkRollCall)) {
            throw new CustomException("不可重复点名");
        }
    }

    @NotNull
    private BdClassRecord addClassRecord(Date startTime, Date endDate, BdClassBO classBO) {
        BdClassRecord bdClassRecord = new BdClassRecord();
        bdClassRecord.setClassId(classBO.getId());
        bdClassRecord.setCourseId(classBO.getClassCourseId());
        bdClassRecord.setShouldNumber(classBO.getExistStudents());
        //设置上课时间开始与结束
        bdClassRecord.setClassStartTime(startTime);
        bdClassRecord.setClassEndTime(endDate);
        // 设置主教老师名称，辅助老师名称，主教老师id
        bdClassRecord.setBishopTeacherId(classBO.getBishopTeacherId());
        bdClassRecord.setBishopTeacherName(classBO.getBishopTeacherName());
        bdClassRecord.setHelpTeacherName(classBO.getHelpTeacherName());
        // 设置校区id 和机构id
        SysUser user = SecurityUtils.getSysUser();
        bdClassRecord.setCampusId(user.getCampusId());
        bdClassRecord.setMecId(user.getMecId());
        bdClassRecord.setIsDelete(Constants.NOT_DELETED);
        bdClassRecord.setCreateTime(DateUtils.getNowDate());
        bdClassRecord.setCreateBy(SecurityUtils.getUserId().toString());
        bdClassRecord.setVersion(Constants.VERSION);
        classRecordService.insert(bdClassRecord);
        return bdClassRecord;
    }


    /**
     * 生成学生的消课记录
     *
     * @param rollCallVO 点名入参
     * @return Integer 实到人数
     */
    public Integer generateClassRecord(BdRollCallVO rollCallVO, BdClassRecord bdClassRecord, BdClassBO classBO, String classTime) {
        // 获取请假的学生信息列表
        List<BdAskLeaveVO> askLeaveList = rollCallVO.getVacateStus();
        //1.生成提前请假学生的课时记录
        for (BdAskLeaveVO askLeaveVO : askLeaveList) {
            BdCallRecordVO callRecord = new BdCallRecordVO();
            callRecord.setClassId(classBO.getId());
            callRecord.setStudentId(askLeaveVO.getStudentId());
            callRecord.setClassRecordId(bdClassRecord.getId());
            callRecord.setType(CallRecordEnum.ADVANCE_STATE.getCode());
            callRecord.setClassTimeNum(CallRecordEnum.CONSUME_TIME_INITIAL.getCode());
            callRecordService.insertBdCallRecord(callRecord);
            //2.为提前请假的学生更新他的消课记录以及上课记录id
            askLeaveVO.setCallRecordId(callRecord.getId());
            askLeaveVO.setClassRecordId(bdClassRecord.getId());
            askLeaveService.updateBdAskLeave(askLeaveVO);
            // 发送消息至家长端
        }
        //3.生成点名时学生的课时记录
        int num = 0;
        List<BdCallRecordVO> callRecordVOList = rollCallVO.getCallRecordList();
        for (BdCallRecordVO callRecordVO : callRecordVOList) {
            if (callRecordVO.getType() == CallRecordEnum.TO_CLASS.getCode()) {
                num++;
            }
            // 目前默认所有状态都是消耗1课次的，之后可能会改，改这里
            // 请假不扣课次
            if (callRecordVO.getType().equals(CallRecordEnum.LEAVE_STATE.getCode())) {
                callRecordVO.setClassTimeNum(CallRecordEnum.CONSUME_TIME_INITIAL.getCode());
            } else {
                callRecordVO.setClassTimeNum(CallRecordEnum.CONSUME_TIME.getCode());
                //更新这个学生该课程的课时 扣课时处理
                BdCourseTimeVO courseTimeVO = new BdCourseTimeVO();
                courseTimeVO.setStudentId(callRecordVO.getStudentId());
                courseTimeVO.setCourseId(classBO.getClassCourseId());
                long courseTimeId = courseTimeService.minusOneCourseTime(courseTimeVO);

                //课时修改记录
                BdCourseTimeUpdateVO courseTimeUpdateVO = new BdCourseTimeUpdateVO();
                courseTimeUpdateVO.setStudentId(callRecordVO.getStudentId());
                courseTimeUpdateVO.setCourseId(classBO.getClassCourseId());
                courseTimeUpdateVO.setChangeType(CourseTimeUpEnum.REDUCE_OPR.getCode());
                courseTimeUpdateVO.setCourseTimeId(courseTimeId);
                courseTimeUpdateVO.setType(CourseTimeUpEnum.DISMISSAL.getCode());
                courseTimeUpdateVO.setReason(CourseTimeUpEnum.DISMISSAL.getInfo());
                courseTimeUpdateVO.setCreateBy(SecurityUtils.getUserId().toString());
                courseTimeUpdateVO.setNum(callRecordVO.getClassTimeNum());
                courseTimeUpdateService.insertBdCourseTimeUpdate(courseTimeUpdateVO);
            }
            callRecordVO.setClassId(classBO.getId());
            callRecordVO.setClassRecordId(bdClassRecord.getId());
            callRecordService.insertBdCallRecord(callRecordVO);

            // 社区版（OS008）：不再向家长端发送模板消息——通知属「暂不承诺」能力，
            // 课消事务本身独立提交，不因通知缺失而失败，也不再依赖小程序 openid。
        }
        return num;
    }

    // 获取该课次详细信息

    /**
     * 查询课程表中课程的详细信息
     *
     * @param classTimeVO 班级上课时间 ID
     * @return 班级上课时间
     */
    public BdClassTimeBO getInfo(ClassTimeVO classTimeVO) {
        BdClassTimeBO classTimeBO = new BdClassTimeBO();
        //获得学生集合
        List<String> stuNames = studentClassService.getAllStudentByClassId(classTimeVO.getClassId());
        classTimeBO.setStuNameList(stuNames);
        return classTimeBO;
    }

    /**
     * 兼容“已含秒”的时间字符串：若已是 yyyy-MM-dd HH:mm:ss 直接返回，否则补 ":00"
     *
     * @param time 时间字符串
     * @return yyyy-MM-dd HH:mm:ss 格式字符串
     */
    private String toFullTimeStr(String time) {
        if (time == null) {
            throw new CustomException("时间参数不能为空");
        }
        if (time.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
            return time;
        }
        if (time.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            return time + ":00";
        }
        throw new CustomException("时间格式不正确：" + time);
    }

    /**
     * 解析“yyyy-MM-dd HH:mm(:ss)”为 Date，解析失败给出友好提示
     *
     * @param time 时间字符串
     * @return Date
     */
    private Date parseFullTime(String time) {
        String full = toFullTimeStr(time);
        try {
            return DateUtils.dateTime("yyyy-MM-dd HH:mm:ss", full);
        } catch (Exception e) {
            throw new CustomException("时间解析失败：" + time);
        }
    }

    /**
     * 获得该班级的学员的信息 （课时信息）
     *
     * @param bdClassTimeVO 班级上课
     * @return List<BdCourseTimeBO>
     */
    public List<BdCourseTimeBO> getStuCourseTimeList(BdClassTimeVO bdClassTimeVO) {
        List<BdCourseTimeBO> courseTimeBOList = new ArrayList<>();

        // 1.找出在班级的所有学生
        List<BdStudentClassBO> list = getStudentByClassId(bdClassTimeVO);
        // 2.将请假的学生从中剔除
        List<BdAskLeaveBO> askLeaveBOList = askLeaveService.selectStudentAtTime(bdClassTimeVO.getEndInWeek(), bdClassTimeVO.getStartInWeek());
        //具有请假人员
        if (askLeaveBOList.size() > 0) {
            Map<Long, Object> askLeaveMap = new HashMap<>();
            for (BdAskLeaveBO askLeaveBO : askLeaveBOList) {
                askLeaveMap.put(askLeaveBO.getStudentId(), askLeaveBO);
            }
            list.removeIf(bdStudentClassBO -> askLeaveMap.containsKey(bdStudentClassBO.getStudentId()));
        }
        // 3.返回这些学员的信息
        Long courseId = classService.selectBaseInfoById(bdClassTimeVO.getClassId()).getClassCourseId();
        for (BdStudentClassBO bo : list) {
            BdCourseTimeVO courseTimeVO = new BdCourseTimeVO();
            courseTimeVO.setStudentId(bo.getStudentId());
            courseTimeVO.setCourseId(courseId);
            BdCourseTimeBO courseTimeBO = courseTimeService.getBdCourseTimeBO(courseTimeVO);
            // 该学员无课次记录时构造默认对象，避免空指针导致点名页打不开
            if (courseTimeBO == null) {
                courseTimeBO = new BdCourseTimeBO();
                courseTimeBO.setStudentId(bo.getStudentId());
                courseTimeBO.setCourseId(courseId);
            }
            String studentName = studentService.selectBdStudentBaseById(bo.getStudentId()).getStudentName();
            courseTimeBO.setStudentName(studentName);
            courseTimeBOList.add(courseTimeBO);
        }
        return courseTimeBOList;
    }


    // 新增课次

    /**
     * 新增课次
     *
     * @param classChangeVO 新增课次信息
     * @return 是否新增成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean insertChangeClass(BdClassChangeVO classChangeVO) {
        Date clsStartTime = classChangeVO.getClsStartTime();
        long currentTimeMillis = System.currentTimeMillis();
        if (clsStartTime.getTime() < currentTimeMillis) {
            throw new CustomException("新增课次要大于当前时间");
        }
        // 1.查询该时间段是否有人上课
        BdClassTimeVO bdClassTimeVO = new BdClassTimeVO();
        bdClassTimeVO.setClsStartTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", classChangeVO.getClsStartTime()));
        bdClassTimeVO.setClsEndTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", classChangeVO.getClsEndTime()));
        bdClassTimeVO.setClassId(classChangeVO.getClassId());
        bdClassTimeVO.setIdType((long) SchoolTableTimeEnum.CHANGE_TYPE.getCode());
        // 2.如果没有则新增，有则说明该时间段不能新增课次
        if (!timeTableDataManager.checkTime(bdClassTimeVO)) {
            classChangeVO.setType(SchoolTableTimeEnum.ADD_LESSON.getCode());
            return classChangeService.insertBdClassChange(classChangeVO);
        }
        return true;
    }


    /**
     * 获取所有班级
     *
     * @param classVO 班级入参
     * @return 班级分页集合
     */
    public List<BdClassBO> selectAllClass(BdClassVO classVO) {
        return classService.selectClassBySchoolTimeTable(classVO);
    }

}
