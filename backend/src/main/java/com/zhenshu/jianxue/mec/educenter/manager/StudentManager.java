package com.zhenshu.jianxue.mec.educenter.manager;

import com.baomidou.mybatisplus.plugins.Page;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.CourseTimeUpEnum;
import com.zhenshu.common.utils.OwnershipGuard;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseBO;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.*;
import com.zhenshu.jianxue.mec.educenter.domain.vo.*;
import com.zhenshu.jianxue.mec.educenter.service.*;
import com.zhenshu.jianxue.mec.educenter.service.impl.BdStudentImport;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author yuxi
 * @desc 学生调用其他表的service
 * @date 2020-09-27
 */
@Service
public class StudentManager {

    @Resource
    private IBdStudentClassService bdStudentClassService;

    @Resource
    private IBdStudentService studentService;

    @Resource
    private IBdClassService bdClassService;

    @Resource
    private IBdCourseTimeService bdCourseTimeService;

    @Resource
    private IBdCourseService bdCourseService;

    @Resource
    private IBdCourseTimeUpdateService bdCourseTimeUpdateService;

    @Resource
    private IBdCoursePurchaseService bdCoursePurchaseService;

    @Resource
    private BdStudentImport studentImport;


    /**
     * 学生导入
     *
     * @param file excel文件
     * @return 是否导入成功
     */
    public String importStudents(MultipartFile file) {
        return studentImport.importStudents(file);
    }

    /**
     * 查询学生的详细信息
     */
    public BdStudentBO selectBdStudentBaseById(Long studentId) {
        BdStudentBO studentBO = studentService.selectBdStudentBaseById(studentId);
        //查学生班级中间表，找出学生所在的班级
        List<BdStudentClassBO> bdStudentClassBOList = getClassesByStuId(studentId);
        StringBuilder builder = new StringBuilder();
        //找出班级的名称
        for (BdStudentClassBO bdStudentClassBO : bdStudentClassBOList) {
            BdClassBO classBO = bdClassService.selectBaseInfoById(bdStudentClassBO.getClassId());
            if (classBO != null && !StringUtils.isEmpty(classBO.getClassName())) {
                builder.append(classBO.getClassName()).append(",");
            }
        }
        if (StringUtils.isEmpty(builder.toString())) {
            studentBO.setClasses("无");
        } else {
            studentBO.setClasses(builder.substring(0, builder.length() - 1));
        }
        return studentBO;
    }

    /**
     * 根据学生得到班级
     *
     * @param studentId 学生id
     * @return List<BdStudentClassBO> 学生所在的班级集合
     */
    public List<BdStudentClassBO> getClassesByStuId(Long studentId) {
        BdStudentClassVO bdStudentClassVO = new BdStudentClassVO();
        bdStudentClassVO.setStudentId(studentId);
        bdStudentClassVO.setIsDelete(Constants.NOT_DELETED);
        bdStudentClassVO.setMecId(SecurityUtils.getLoginMecId());
        bdStudentClassVO.setCampusId(SecurityUtils.getLoginCampusId());
        return bdStudentClassService.getAll(bdStudentClassVO);
    }

    /**
     * 根据学生获取到他的课次修改记录
     *
     * @param bdStudentVO 学生
     * @return List<UpCouInfoStuBO> 该学生的修改课次操作记录
     */
    public List<UpCouInfoStuBO> getCourseTimeUpdateByStuId(BdStudentVO bdStudentVO) {
        return bdCourseTimeUpdateService.selectBdCourseTimeUpdateList(bdStudentVO.getId());
    }

    /**
     * 查询课程购买记录
     *
     * @param bdStudentVO 学生id
     * @return CoursePurchaseStu
     */
    public List<BdCoursePurchaseBO> getInfoWithCoursePurchase(BdStudentVO bdStudentVO) {
        return bdCoursePurchaseService.selectListByStudentId(bdStudentVO.getId());
    }

    /**
     * 修改课次
     *
     * @param courseTimeUpdateVO 修改课次
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCourseTime(BdCourseTimeUpdateVO courseTimeUpdateVO) {
        courseTimeUpdateVO.setType(CourseTimeUpEnum.HAND_WORK.getCode());
        createCourseTimeUpdate(courseTimeUpdateVO);
        // 更新课次
        bdCourseTimeService.updateCourseTimeById(courseTimeUpdateVO);
    }

    /**
     * 修改总课次
     * @param courseTimeUpdateVO 修改课次信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTotalCourseTime(BdCourseTimeUpdateVO courseTimeUpdateVO) {
        courseTimeUpdateVO.setType(CourseTimeUpEnum.HAND_CHANGE_TOTAL.getCode());
        BdCourseTimeBO courseTimeBO = createCourseTimeUpdate(courseTimeUpdateVO);
        if (courseTimeBO.getRemainingNumber() > courseTimeUpdateVO.getNum()){
            throw new CustomException("修改总课次不可低于剩余课次");
        }
        // 更新课次
        bdCourseTimeService.updateTotalCourseTime(courseTimeUpdateVO);
    }

    public BdCourseTimeBO createCourseTimeUpdate(BdCourseTimeUpdateVO courseTimeUpdateVO) {
        SysUser user = SecurityUtils.getSysUser();
        courseTimeUpdateVO.setMecId(user.getMecId());
        courseTimeUpdateVO.setCampusId(user.getCampusId());
        courseTimeUpdateVO.setCreateTime(new Date());
        courseTimeUpdateVO.setCreateBy(SecurityUtils.getUserId().toString());

        // 获得学生id
        BdCourseTimeBO courseTimeBO = bdCourseTimeService.selectBdCourseTimeById(courseTimeUpdateVO.getCourseTimeId());
        // OS023 归属守卫：目标课时账户必须属于当前会话机构/校区
        OwnershipGuard.checkOwned(courseTimeBO.getMecId(), courseTimeBO.getCampusId());
        courseTimeUpdateVO.setCourseId(courseTimeBO.getCourseId());
        courseTimeUpdateVO.setStudentId(courseTimeBO.getStudentId());
        // 新增一条课次修改记录
        bdCourseTimeUpdateService.insertBdCourseTimeUpdate(courseTimeUpdateVO);

        return courseTimeBO;
    }

    /**
     * 学员新增课程购买记录
     *
     * @param bdCoursePurchaseVO 增课程购买记录信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertBdCoursePurchase(BdCoursePurchaseVO bdCoursePurchaseVO) {
        //新增课程购买记录
        bdCoursePurchaseService.insertBdCoursePurchase(bdCoursePurchaseVO);
        //增加课次
        Long studentId = bdCoursePurchaseVO.getStudentId();
        Long courseId = bdCoursePurchaseVO.getCourseId();
        Integer num = bdCoursePurchaseVO.getTimeNum();
        // OS023 归属守卫：学员与课程都必须属于当前会话机构/校区
        BdStudentBO studentBO = studentService.selectBdStudentBaseById(studentId);
        if (studentBO != null) {
            OwnershipGuard.checkOwned(studentBO.getMecId(), studentBO.getCampusId());
        }
        BdCourseBO courseBO = bdCourseService.selectBdCourseById(courseId);
        if (courseBO != null) {
            OwnershipGuard.checkOwned(courseBO.getMecId(), courseBO.getCampusId());
        }
        if (studentBO == null || courseBO == null) {
            throw new CustomException("学员或课程不存在");
        }

        BdCourseTimeVO bdCourseTimeVO = new BdCourseTimeVO();
        bdCourseTimeVO.setCourseId(courseId);
        bdCourseTimeVO.setStudentId(studentId);
        Long courseTimeId = bdCourseTimeService.updateByCoursePurchase(bdCourseTimeVO, num);
        //课次更新记录
        BdCourseTimeUpdateVO courseTimeUpdateVO = new BdCourseTimeUpdateVO();
        courseTimeUpdateVO.setCourseId(bdCoursePurchaseVO.getCourseId());
        courseTimeUpdateVO.setChangeType(CourseTimeUpEnum.ADD_OPR.getCode());
        courseTimeUpdateVO.setType(CourseTimeUpEnum.PURCHASE_IN_CLASS.getCode());
        courseTimeUpdateVO.setNum(bdCoursePurchaseVO.getTimeNum());
        courseTimeUpdateVO.setStudentId(studentId);
        courseTimeUpdateVO.setCourseTimeId(courseTimeId);
        courseTimeUpdateVO.setReason(CourseTimeUpEnum.PURCHASE_REASON.getInfo());
        bdCourseTimeUpdateService.insertBdCourseTimeUpdate(courseTimeUpdateVO);
    }

    /**
     * 获取所有有效课程
     *
     * @param bdCourseVO 课程入参VO
     * @return 有效课程集合
     */
    public List<BdCourseBO> selectAllCourse(BdCourseVO bdCourseVO) {
        return bdCourseService.selectAll(bdCourseVO);
    }

    /**
     * 得到该学生在读的课程
     *
     * @param bdStudentVO 学生信息
     * @return 学生分页的课次信息
     */
    public List<CourseInfoStuBO> getCourseTimeByStuId(BdStudentVO bdStudentVO) {
        return studentService.selectCourseTimeDetailList(bdStudentVO.getId());
    }

    /**
     * 查询学生列表
     *
     * @param bdStudentVO 学生信息
     * @return 学生列表集合
     */
    public Page<BdStudentBO> selectBdStudentList(BdStudentVO bdStudentVO) {
        Page<BdStudentBO> page = studentService.selectBdStudentList(bdStudentVO);
        List<BdStudentBO> bdStudentBOList = page.getRecords();
        if (bdStudentBOList == null || bdStudentBOList.size() <= 0) {
            return null;
        }
        List<Long> studentIdList = new ArrayList<>();
        for (BdStudentBO studentBO : bdStudentBOList) {
            studentIdList.add(studentBO.getId());
        }
        // 根据学员id 列表，查出班级列表
        List<BdStudentClassBO> studentClassList = bdStudentClassService.selectClassByStudentIds(studentIdList);
        // 组装班级名称
        for (BdStudentBO studentBO : bdStudentBOList) {
            StringJoiner stringJoiner = new StringJoiner(",");
            for (BdStudentClassBO studentClass : studentClassList) {
                if (studentBO.getId().equals(studentClass.getStudentId())) {
                    stringJoiner.add(studentClass.getClassName());
                }
            }
            studentBO.setClasses(stringJoiner.toString());
        }
        return page;
    }

    /**
     * 删除学生
     *
     * @param ids 学生id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Long[] ids) {
        List<BdStudentBO> studentList = studentService.getStudentListByIds(ids);
        if (studentList.size() != ids.length){
            throw new CustomException("未查询到该在读学生的信息，请稍后重试");
        }
        for (Long id : ids) {
            //查询该学生是否有班级，如果有班级则不能删除
            List<Long> studentIdList = new ArrayList<>();
            studentIdList.add(id);
            List<BdStudentClassBO> studentClassList = bdStudentClassService.selectClassByStudentIds(studentIdList);
            if (studentClassList != null && studentClassList.size() > 0) {
                throw new CustomException("已有班级的学员不可删除");
            }
            studentService.deleteBdStudentById(id);
        }
    }

    /**
     * 学员课时账户导出（OS024，C14）：机构/校区归属取会话，学员姓名支持模糊筛选。
     */
    public java.util.List<com.zhenshu.jianxue.mec.educenter.domain.vo.StudentExportVO> exportStudents(BdStudentVO bdStudentVO) {
        SysUser user = SecurityUtils.getSysUser();
        return studentService.selectStudentExportList(user.getMecId(), user.getCampusId(), bdStudentVO.getStudentName());
    }

    /**
     * 更新学生
     * @param bdStudentVO 学生信息
     * @return 是否更新成功
     */
    public Boolean updateBdStudent(BdStudentVO bdStudentVO) {
        BdStudentBO studentBO = studentService.selectBdStudentBaseById(bdStudentVO.getId());
        if (studentBO == null){
            throw new CustomException(String.format("学生id:%s不存在", bdStudentVO.getId()));
        }
        if (!bdStudentVO.getStudentsPhone().equals(studentBO.getStudentsPhone())){
            BdStudentBO byPhone = studentService.selectBdStudentBaseByPhone(bdStudentVO.getStudentsPhone());
            if (byPhone != null){
                throw new CustomException("手机号码已重复");
            }
        }
        return studentService.updateBdStudent(bdStudentVO);
    }

    /**
     * 恢复学员
     * @param id 学生id
     * @return 是否恢复成功
     */
    public Boolean updateRecoverRec(Long id) {
        // 获得该学员的信息
        BdStudentBO studentBO = studentService.selectBdStudentBaseById(id);
        if (studentBO == null){
            throw new CustomException(String.format("学生id:%s不存在", id));
        }
        String phone = studentBO.getStudentsPhone();
        // 判断这个手机号在正常学员中是否存在
        BdStudentBO boByPhone = studentService.selectBdStudentBaseByPhone(phone);
        if (boByPhone != null){
            throw new CustomException("该手机号的学员在正常学员中已存在，不可恢复");
        }
        return studentService.updateRecoverRec(id);
    }
}
