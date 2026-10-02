package com.zhenshu.jianxue.mec.educenter.manager;

import com.baomidou.mybatisplus.plugins.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.ClassEnum;
import com.zhenshu.common.enums.educenter.CourseTimeEnum;
import com.zhenshu.common.enums.educenter.TeacherEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.*;
import com.zhenshu.jianxue.mec.educenter.domain.vo.*;
import com.zhenshu.jianxue.mec.educenter.service.*;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.mapper.domain.educenter.BdClass;
import com.zhenshu.mapper.domain.educenter.BdStudentClass;
import com.zhenshu.mapper.domain.educenter.BdTeacher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author yuxi
 * @version 1.0
 * @desc 班级调用其他表
 * @date
 */
@Service
public class ClassManager {

    @Resource
    private IBdCourseService bdCourseService;
    @Resource
    private IBdTeacherService teacherService;
    @Resource
    private IBdClassTimeService classTimeService;
    @Resource
    private IBdCourseTimeService courseTimeService;
    @Resource
    private IBdStudentClassService studentClassService;
    @Resource
    private IBdStudentService studentService;
    @Resource
    private com.zhenshu.mapper.educenter.BdStudentMapper bdStudentMapper;
    @Resource
    private IBdClassChangeService classChangeService;
    @Resource
    private IBdClassService classService;
    @Resource
    private CommonManager commonManager;


    /**
     * 查询常规班级列表
     *
     * @param bdClassVO 班级
     * @return 班级
     */
    public List<BdClassBO> selectConventionalClsList(BdClassVO bdClassVO) {
        setVoId(bdClassVO);
        // 查询班级类型为常规班级的
        bdClassVO.setClassType(ClassEnum.ORDINARY.getCode());
        return getClassListInfo(bdClassVO);
    }

    /**
     * 获取班级详细信息，包括上课时间
     *
     * @param bdClassVO 班级传参
     * @return 班级信息
     */
    public List<BdClassBO> getClassListInfo(BdClassVO bdClassVO) {
        List<BdClassBO> classList = classService.getClassDetailedList(bdClassVO);
        // 获取班级id 列表
        Long[] classIds = new Long[classList.size()];
        int num = 0;
        for (BdClassBO classBO : classList) {
            classIds[num] = classBO.getId();
            num += 1;
        }
        // 获取班级相关的上课时间
        Map<Long, List<String>> classTimeMap = classTimeService.selectListByClassIds(classIds);
        for (BdClassBO classBO : classList) {
            // 根据班级id获取班级上课时间
            List<String> classTimeList = classTimeMap.get(classBO.getId());
            classBO.setClassTimes(classTimeList);
        }
        return classList;

    }


    /**
     * 为入参VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdClassVO 入参PO
     * @return BdClass
     */
    public BdClassVO setVoId(BdClassVO bdClassVO) {
        SysUser user = SecurityUtils.getSysUser();
        bdClassVO.setCampusId(user.getCampusId());
        bdClassVO.setMecId(user.getMecId());
        bdClassVO.setIsDelete(Constants.NOT_DELETED);
        return bdClassVO;
    }


    /**
     * 查询班级详细信息，包括上课时间表，课程名称，主教老师名称，辅助老师名称
     *
     * @param id 班级ID
     * @return 班级信息
     */
    public BdClassBO selectBdClassDetailedById(Long id) {
        return commonManager.selectBdClassDetailedById(id);
    }

    /**
     * 新增班级
     *
     * @param bdClassVO 班级信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertBdClass(BdClassVO bdClassVO) {
        // B-8：原实现未填充机构/校区归属，新增班级落库后 mec_id/campus_id 均为 NULL，
        // 校区维度列表查不到该班级（与课程新增同类问题）。这里与查询口径统一填充。
        setVoId(bdClassVO);
        classService.insertBdClass(bdClassVO);
        //上课时间表
        List<BdClassTimeVO> list = bdClassVO.getClassTimeList();
        // B-7：classTimeList 缺省时为 null，原实现直接遍历抛 NPE，被兜底成 500 且整条事务回滚
        if (list != null) {
            for (BdClassTimeVO vo : list) {
                vo.setClassId(bdClassVO.getId());
                classTimeService.insertBdClassTime(vo);
            }
        }
        //修改老师的已授班级数
        teacherService.updateTeacherTaughtCls(bdClassVO.getBishopTeacherId(), TeacherEnum.ADD_CLASS_TAUGHT.getMsg());
    }

    /**
     * 修改班级
     *
     * @param bdClassVO 班级信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBdClass(BdClassVO bdClassVO) {
        BdClass bdClass = voToPo(bdClassVO);
        BdClassBO original = classService.selectBaseInfoById(bdClassVO.getId());
        //判断查询出主教老师与入参的主教id是否相等
        if (!original.getBishopTeacherId().equals(bdClassVO.getBishopTeacherId())) {
            // 原本的老师已授班级数减一
            teacherService.updateTeacherTaughtCls(original.getBishopTeacherId(), TeacherEnum.REDUCE_CLASS_TAUGHT.getMsg());
            // 现在的老师已授班级数加一
            teacherService.updateTeacherTaughtCls(bdClassVO.getBishopTeacherId(), TeacherEnum.ADD_CLASS_TAUGHT.getMsg());
        }
        //更新班级信息
        bdClass.setUpdateTime(DateUtils.getNowDate());
        bdClass.setUpdateBy(SecurityUtils.getUserId().toString());
        classService.updateById(bdClass);
        //修改班级上课时间表
        updateClassTime(bdClassVO);
    }

    /**
     * 班级修改上课时间
     *
     * @param bdClassVO 班级入参VO
     */
    public void updateClassTime(BdClassVO bdClassVO) {
        //1.查询该班级的所有上课
        List<BdClassTimeBO> classTimeBOList = classTimeService.getClassTimeByClassId(bdClassVO.getId());
        Map<Integer, BdClassTimeBO> classTimeBoMap = new HashMap<>();
        for (BdClassTimeBO bo : classTimeBOList) {
            classTimeBoMap.put(bo.getWeekDay(), bo);
        }
        //获取修改的上课时间
        List<BdClassTimeVO> classTimeVoS = bdClassVO.getClassTimeList();
        // OS012B：与新增班级的 B-7 同款 —— 缺省时原实现直接遍历抛 NPE，
        // 被兜底成 500 且整条事务回滚。这里明确为「不传即不改课表」，只更新班级自身字段。
        if (classTimeVoS == null) {
            return;
        }
        //给予一个是否修改的表示
        Set<Integer> updateIdSet = new HashSet<>();
        for (BdClassTimeVO classTimeVO : classTimeVoS) {
            //2.判断周几 --> 如果包含了就说明是修改，没有包含就是新增
            if (classTimeBoMap.containsKey(classTimeVO.getWeekDay())) {
                classTimeService.updateById(classTimeVO);
                //将原有的进行更新存入map集合
                updateIdSet.add(classTimeVO.getWeekDay());
            } else {
                classTimeVO.setClassId(bdClassVO.getId());
                classTimeService.insertBdClassTime(classTimeVO);
            }
        }

        for (Map.Entry<Integer, BdClassTimeBO> m : classTimeBoMap.entrySet()) {
            //3.更新map没有包含则是说明了是删除的
            if (!updateIdSet.contains(m.getKey())) {
                //a.将该班级上课时间在进行删除，返回当前的上课时间的id
                BdClassTimeBO classTimeBO = m.getValue();
                classTimeService.deleteBdClassTimeById(classTimeBO.getId());
                //这条上课时间 对应的调课同步取消，只取消大于当天的记录，已上过课的不取消
                classChangeService.deleteClassChangeByClassTimeId(classTimeBO.getId());
            }
        }
    }


    /**
     * 获得该班级的学生
     *
     * @param bdClassVO 班级入参VO 分页
     * @return List<BdStudentBO>
     */
    public List<BdStudentBO> getStudentsByClsWithPage(BdClassVO bdClassVO) {
        BdClassBO classBO = classService.selectBaseInfoById(bdClassVO.getId());
        bdClassVO.setClassCourseId(classBO.getClassCourseId());
        //先找到在该班级的学生
        PageHelper.startPage(bdClassVO.getPageNum(), bdClassVO.getPageSize());
        return studentClassService.selectStudentByClass(bdClassVO);
    }


    /**
     * 查找所有上架的课程 不分体验还是常规
     *
     * @param bdCourseVO 课程
     * @return 课程集合
     */
    public Page<BdCourseBO> selectAllCourse(BdCourseVO bdCourseVO) {
        return bdCourseService.selectAllCourseByPage(bdCourseVO);
    }

    /**
     * 查询老师列表
     *
     * @param bdTeacherVO 老师
     * @return 老师集合
     */
    public Page<BdTeacherBO> selectBdTeacherList(BdTeacherVO bdTeacherVO) {
        // 只查询在在职老师
        bdTeacherVO.setIsDeparture(TeacherEnum.ON_JOB.getCode());
        Page<BdTeacherBO> teacherPage = teacherService.selectBdTeacherList(bdTeacherVO);

        // 获得老师教授学员总数
        List<BdTeacherBO> teacherList = teacherPage.getRecords();

        if (teacherList.isEmpty()) {
            return null;
        }

        List<Long> teacherIdList = new ArrayList<>();
        for (BdTeacherBO teacherBO : teacherList) {
            teacherIdList.add(teacherBO.getId());
        }

        Map<Long, Integer> map = classService.getTotalTaught(teacherIdList);

        for (BdTeacherBO teacherBO : teacherList) {
            if (map.containsKey(teacherBO.getId())) {
                teacherBO.setTaughtStuNum(map.get(teacherBO.getId()));
            }
        }
        return teacherPage;
    }

    /**
     * 作废班级
     * 校验是否可以删除 不允许删除已有学员的班级，已授课的班级不可废弃
     *
     * @param classIds 班级id
     */
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long[] classIds) {
        if (classIds.length <= 0) {
            throw new CustomException("作废班级id不能为空");
        }

        List<BdClassBO> classList = classService.getClassInfoByClassIds(Arrays.asList(classIds));
        if (classList.size() != classIds.length) {
            throw new CustomException("未查询到该班级，请稍后重试");
        }

        for (Long classId : classIds) {
            BdClassBO classBO = classService.selectBaseInfoById(classId);
            // 已授课的班级不可废弃，避免已扣课次无法追溯
            if (classBO.getHasClass() != null && classBO.getHasClass() > 0) {
                throw new CustomException("该班级已有授课记录，不可废弃");
            }
            List<BdStudentClassBO> studentClassList = studentClassService.getAllByClassId(classId);
            if (studentClassList.size() > 0) {
                throw new CustomException("不允许废弃仍有学员的班级");
            }
            // 清理可能残留的 student_class 关联，避免孤儿数据
            for (BdStudentClassBO studentClassBO : studentClassList) {
                BdStudentClassVO deleteVO = new BdStudentClassVO();
                deleteVO.setClassId(classId);
                deleteVO.setStudentId(studentClassBO.getStudentId());
                studentClassService.deleteStu(deleteVO);
            }
            // 更新主教老师已授班级数
            Long bishop = classBO.getBishopTeacherId();
            Integer count = classService.getBishopClassCount(bishop);
            if (count != 0) {
                Integer bishopCount = count - 1;
                BdTeacher teacher = new BdTeacher();
                teacher.setId(bishop);
                teacher.setTaughtNum(bishopCount);
                teacherService.updateById(teacher);
            }
            classService.deleteBdClassById(classId);
        }
    }


    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdClassVO 入参VO
     * @return bdCallRecord
     */
    public BdClass voToPo(BdClassVO bdClassVO) {
        BdClass bdClass = new BdClass();
        BeanUtils.copyBeanProp(bdClass, bdClassVO);
        SysUser user = SecurityUtils.getSysUser();
        bdClass.setCampusId(user.getCampusId());
        bdClass.setMecId(user.getMecId());
        bdClass.setIsDelete(Constants.NOT_DELETED);
        return bdClass;
    }


    /**
     * 班级添加学生
     *
     * @param bdClassVO 班级
     */
    @Transactional(rollbackFor = Exception.class)
    public void addStuClass(BdClassVO bdClassVO) {
        Long classId = bdClassVO.getId();
        Long[] stuIds = bdClassVO.getStuIds();

        if (stuIds.length <= 0) {
            throw new CustomException("新增学员不可为空");
        }

        // 1.判断人数是否已满
        BdClassBO classBO = classService.selectBaseInfoById(classId);
        if (classBO.getExistStudents() >= classBO.getSumStudents()) {
            throw new CustomException("该班级人数已满");
        }

        int remain = classBO.getSumStudents() - classBO.getExistStudents();
        if (stuIds.length > remain) {
            throw new CustomException("添加人数大于该班级剩余可容纳数");
        }

        // 校验学生是否存在（按当前登录机构/校区过滤，防止跨校区 enrol）
        List<BdStudentBO> studentListByIds = bdStudentMapper.getStudentListByIdsWithScope(stuIds, SecurityUtils.getLoginMecId(), SecurityUtils.getLoginCampusId());
        if (studentListByIds.size() != stuIds.length) {
            Map<Long, BdStudentBO> stuMap = new HashMap<>(stuIds.length);
            for (BdStudentBO studentBO : studentListByIds) {
                stuMap.put(studentBO.getId(), studentBO);
            }
            StringJoiner stringJoiner = new StringJoiner(",");
            for (Long stuId : stuIds) {
                if (!stuMap.containsKey(stuId)) {
                    stringJoiner.add(stuId.toString());
                }
            }
            throw new CustomException("学生id为：" + stringJoiner.toString() + "不存在");
        }

        // 2.判断添加的学生是否存在于该班级
        for (Long stuId : stuIds) {
            BdStudentClassVO studentClassVO = new BdStudentClassVO();
            studentClassVO.setClassId(classId);
            studentClassVO.setStudentId(stuId);
            BdStudentClassBO studentClass = studentClassService.selectByStuCls(studentClassVO);
            if (studentClass != null) {
                BdStudentBO student = studentService.selectBdStudentBaseById(stuId);
                throw new CustomException(student.getStudentName() + "存在于该班级");
            }
        }

        // 3.班级添加学生
        for (Long stuId : stuIds) {
            BdStudentClassVO studentClassVO = new BdStudentClassVO();
            studentClassVO.setClassId(classId);
            studentClassVO.setStudentId(stuId);
            studentClassService.insertBdStudentClass(studentClassVO);
        }

        // 4.为学生添加课时
        for (Long stuId : stuIds) {
            BdCourseTimeVO courseTimeVO = new BdCourseTimeVO();
            courseTimeVO.setCourseId(classBO.getClassCourseId());
            courseTimeVO.setStudentId(stuId);
            BdCourseTimeBO courseTime = courseTimeService.selectBdCourseTimeByVO(courseTimeVO);
            if (courseTime == null) {
                BdCourseTimeVO bdCourseTimeVO = new BdCourseTimeVO();
                bdCourseTimeVO.setCourseId(classBO.getClassCourseId());
                bdCourseTimeVO.setStudentId(stuId);
                bdCourseTimeVO.setRemainingNumber(CourseTimeEnum.INITIAL.getCode());
                bdCourseTimeVO.setTotalNumber(CourseTimeEnum.INITIAL.getCode());
                bdCourseTimeVO.setIsDelete(Constants.NOT_DELETED);
                courseTimeService.insertBdCourseTime(bdCourseTimeVO);
            }
        }

        // 5.更新班级已有人数
        BdClass bdClass = classService.selectById(bdClassVO.getId());
        // 5.1 查询学生班级中间表，该班级有多少人
        Integer number = getStudentsByCls(bdClassVO.getId());
        bdClass.setExistStudents(number);
        // 5.2 更新班级的已有人数
        classService.updateById(bdClass);
    }


    /**
     * 查询该班级的人数
     *
     * @param classId 班级id
     * @return 班级人数
     */
    public Integer getStudentsByCls(Long classId) {
        BdStudentClassVO studentClassVO = new BdStudentClassVO();
        studentClassVO.setClassId(classId);
        studentClassVO.setIsDelete(Constants.NOT_DELETED);
        List<BdStudentClassBO> list = studentClassService.getAll(studentClassVO);
        if (list == null || list.isEmpty()) {
            return 0;
        }
        return list.size();
    }

    /**
     * 班级移除学生
     *
     * @param studentClassVO 班级学生信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void classRemoveStu(BdStudentClassVO studentClassVO) {
        Long classId = studentClassVO.getClassId();
        List<Long> studentIds = studentClassVO.getStudentIds();
        if (studentIds == null || studentIds.size() <= 0) {
            throw new CustomException("删除人数可小于0");
        }
        // 1.删除学生班级的记录
        for (Long studentId : studentIds) {
            BdStudentClassVO deleteStudent = new BdStudentClassVO();
            SysUser sysUser = SecurityUtils.getSysUser();
            deleteStudent.setCampusId(sysUser.getCampusId());
            deleteStudent.setMecId(sysUser.getMecId());
            deleteStudent.setStudentId(studentId);
            deleteStudent.setClassId(classId);
            studentClassService.deleteStu(deleteStudent);
        }
        //更新班级人数数量
        BdClass bdClass = classService.selectById(classId);
        // 2.查询学生班级中间表，该班级有多少人
        Integer number = getStudentsByCls(classId);
        bdClass.setExistStudents(number);
        // 3.更新班级的已有人数
        classService.updateById(bdClass);
    }

    /**
     * 得到未在该班级的学生课次信息
     *
     * @param bdClassVO 班级信息 班级id
     * @return 未在该班级的学生课次信息
     */
    public PageInfo<BdCourseTimeBO> getStudents(BdClassVO bdClassVO) {
        // 设置机构id与校区id
        bdClassVO.setCampusId(SecurityUtils.getLoginCampusId());
        bdClassVO.setMecId(SecurityUtils.getLoginMecId());
        // 1.得到不在该班级的学生以及数量
        List<BdStudentBO> studentList = studentService.getStudentList(bdClassVO);
        PageInfo<BdStudentBO> studentPageInfo = new PageInfo<>(studentList);

        BdClass bdClass = classService.selectById(bdClassVO.getId());

        Long[] studentIds = new Long[studentList.size()];
        int num = 0;
        for (BdStudentBO studentBO : studentList) {
            studentIds[num] = studentBO.getId();
            num += 1;
        }
        // 根据学员id列表 查到学员 该班级的课程的课次信息
        Map<Long, BdCourseTimeBO> courseTimeBoMap = courseTimeService.selectByStuIdsAndCourseId(studentIds, bdClass.getClassCourseId());
        List<BdCourseTimeBO> courseTimeList = new ArrayList<>();
        for (BdStudentBO studentBO : studentList) {
            BdCourseTimeBO courseTimeBO = courseTimeBoMap.get(studentBO.getId());
            // 如果没有查到，那么新增一个课次信息记录
            if (courseTimeBO == null) {
                BdCourseTimeVO bdCourseTimeVO = new BdCourseTimeVO();
                bdCourseTimeVO.setStudentId(studentBO.getId());
                bdCourseTimeVO.setCourseId(bdClass.getClassCourseId());
                bdCourseTimeVO.setRemainingNumber(CourseTimeEnum.INITIAL.getCode());
                bdCourseTimeVO.setTotalNumber(CourseTimeEnum.INITIAL.getCode());
                courseTimeBO = new BdCourseTimeBO();
                BeanUtils.copyBeanProp(courseTimeBO, bdCourseTimeVO);
            }
            // 设置课次记录里的学员信息
            courseTimeBO.setStudentName(studentBO.getStudentName());
            courseTimeBO.setStudentPhone(studentBO.getStudentsPhone());
            courseTimeList.add(courseTimeBO);
        }

        PageInfo<BdCourseTimeBO> studentWithCourseTimePage = new PageInfo<>();
        studentWithCourseTimePage.setTotal(studentPageInfo.getTotal());
        studentWithCourseTimePage.setList(courseTimeList);
        return studentWithCourseTimePage;
    }

    /**
     * 获得其他班级
     *
     * @param bdClassVO 当前班级id
     * @return 其他班级列表
     */
    public Page<BdClassBO> selectOtherClass(BdClassVO bdClassVO) {
        BdClassBO classBO = classService.selectBaseInfoById(bdClassVO.getId());
        if (classBO == null) {
            throw new CustomException("当前班级id不可为空");
        }
        return classService.selectOtherClass(bdClassVO);
    }

    /**
     * 其他班级的学生导入该班级
     *
     * @param importClassId   导入学生的班级id
     * @param importedClassId 被导入学生的班级id
     */
    @Transactional(rollbackFor = Exception.class)
    public String stuToThisClass(Long importClassId, Long importedClassId) {
        // 1.获得被导入学生班级的学生数量，以及可容纳的数量
        BdClassBO importedClass = classService.selectBaseInfoById(importedClassId);
        if (importedClass == null) {
            throw new CustomException("未查询到被导入学生班级的信息");
        }
        Integer existStudents = importedClass.getExistStudents();
        Integer sumStudents = importedClass.getSumStudents();
        if (existStudents.equals(sumStudents)){
            throw new CustomException("该班级人数已满，不可进行导入");
        }
        BdClassBO importClass = classService.selectBaseInfoById(importClassId);
        if (importClass == null) {
            throw new CustomException("未查询到导入学生班级的信息");
        }

        // 2.获得两个班级的学生，进行分组
        List<Long> classIdList = new ArrayList<>();
        classIdList.add(importClassId);
        classIdList.add(importedClassId);
        List<BdStudentClass> studentClassList = studentClassService.selectStuByClassIds(classIdList);
        Map<Long, List<Long>> classMap = new HashMap<>();
        for (BdStudentClass bdStudentClass : studentClassList) {
            Long classId = bdStudentClass.getClassId();
            if (classMap.containsKey(classId)) {
                List<Long> list = classMap.get(classId);
                list.add(bdStudentClass.getStudentId());
            } else {
                List<Long> list = new ArrayList<>();
                list.add(bdStudentClass.getStudentId());
                classMap.put(classId, list);
            }
        }

        // 3.获得导入班级中的学生不存在被导入班级的学生id集合
        List<Long> notExistStu = new ArrayList<>();
        List<Long> importList = classMap.get(importClassId);
        List<Long> importedList = classMap.get(importedClassId);
        // 源班级或目标班级无学生时 classMap 取不到对应集合，置空避免空指针
        if (importList == null) {
            importList = new ArrayList<>();
        }
        if (importedList == null) {
            importedList = new ArrayList<>();
        }
        for (Long studentId : importList) {
            if (!importedList.contains(studentId)) {
                notExistStu.add(studentId);
            }
        }
        if (notExistStu.size() == 0){
             throw new CustomException("导入班级中的学生都存在于被导入的班级");
        }

        // 4.导入学生
        boolean isFull = false;
        String msg;
        int importCount = 0;
        for (Long studentId : notExistStu) {
            if (existStudents.equals(sumStudents)) {
                isFull = true;
                break;
            }
            BdStudentClassVO studentClassVO = new BdStudentClassVO();
            studentClassVO.setClassId(importedClassId);
            studentClassVO.setStudentId(studentId);
            studentClassService.insertBdStudentClass(studentClassVO);
            existStudents++;
            importCount++;
        }

        // 5.更新班级已存在人数
        BdClass bdClass = new BdClass();
        bdClass.setId(importedClassId);
        bdClass.setExistStudents(existStudents);
        classService.updateById(bdClass);
        if (isFull) {
            int notExistSize = notExistStu.size();
            msg = String.format("导入%s名学生成功，由于班级人数已满，剩余未存在于该班级%s名未导入", importCount, notExistSize - importCount);
        } else {
            msg = "导入成功";
        }

        return msg;
    }
}
