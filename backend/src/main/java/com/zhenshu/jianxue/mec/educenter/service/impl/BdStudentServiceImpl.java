package com.zhenshu.jianxue.mec.educenter.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.StudentEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.CourseInfoStuBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdStudentVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdStudentService;
import com.zhenshu.mapper.domain.educenter.BdStudent;
import com.zhenshu.mapper.educenter.BdStudentMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

/**
 * 学生Service业务层处理
 *
 * @author yuxi
 * @date 2020-09-25
 */
@Service
public class BdStudentServiceImpl extends ServiceImpl<BdStudentMapper, BdStudent> implements IBdStudentService {

    @Resource
    BdStudentMapper studentMapper;

    /**
     * 查询学生基本信息
     *
     * @param id 学生ID
     * @return 学生
     */
    @Override
    public BdStudentBO selectBdStudentBaseById(Long id) {
        BdStudent bdStudent = this.selectById(id);
        if (bdStudent == null) {
            return null;
        }
        // 多租户隔离：校验学员归属当前登录机构，防止跨租户越权读取
        checkStudentBelong(bdStudent);
        BdStudentBO bdStudentBO = new BdStudentBO();
        BeanUtils.copyBeanProp(bdStudentBO, bdStudent);
        return bdStudentBO;
    }

    /**
     * 校验学员数据是否属于当前登录机构，防止水平越权（跨租户访问他人数据）
     *
     * @param bdStudent 学员
     */
    private void checkStudentBelong(BdStudent bdStudent) {
        if (bdStudent == null) {
            return;
        }
        // 平台账号的机构id为空，不做限制；机构/校区账号必须与数据归属一致
        Long loginMecId = SecurityUtils.getLoginMecId();
        if (loginMecId != null && bdStudent.getMecId() != null && !loginMecId.equals(bdStudent.getMecId())) {
            throw new CustomException("无权访问该学员数据");
        }
    }

    /**
     * 查询学生信息
     *
     * @param phone 学生手机号
     * @return 学生信息
     */
    @Override
    public BdStudentBO selectBdStudentBaseByPhone(String phone) {
        SysUser sysUser = SecurityUtils.getSysUser();
        EntityWrapper<BdStudent> wrapper = new EntityWrapper<>();
        wrapper.eq("students_phone", phone);
        wrapper.eq("mec_id", sysUser.getMecId());
        wrapper.eq("campus_id", sysUser.getCampusId());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        wrapper.eq("is_ordinary", StudentEnum.NORMAL_STU.getCode());
        BdStudent bdStudent = this.selectOne(wrapper);
        if (bdStudent == null) {
            return null;
        }
        BdStudentBO studentBO = new BdStudentBO();
        BeanUtils.copyBeanProp(studentBO, bdStudent);
        return studentBO;
    }

    /**
     * 按手机号查询学员（含回收站学员），用于新增时的手机号唯一性校验
     *
     * @param phone 学生手机号
     * @return 学员
     */
    private BdStudent selectByPhoneWithRecycle(String phone) {
        SysUser sysUser = SecurityUtils.getSysUser();
        EntityWrapper<BdStudent> wrapper = new EntityWrapper<>();
        wrapper.eq("students_phone", phone);
        wrapper.eq("mec_id", sysUser.getMecId());
        wrapper.eq("campus_id", sysUser.getCampusId());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        return this.selectOne(wrapper);
    }

    /**
     * 查询在读学员列表
     *
     * @param bdStudentVO 学生
     * @return 在读学员集合
     */
    @Override
    public Page<BdStudentBO> selectBdStudentList(BdStudentVO bdStudentVO) {
        BdStudent bdStudent = new BdStudent();
        setPoId(bdStudent);
        EntityWrapper<BdStudent> wrapper = new EntityWrapper<>();
        //表示在读学员
        bdStudent.setIsOrdinary(StudentEnum.NORMAL_STU.getCode());

        return getPage(bdStudent, bdStudentVO, wrapper);
    }

    /**
     * 查询回收站学员
     *
     * @param bdStudentVO 学生
     * @return 回收站学员集合
     */
    @Override
    public Page<BdStudentBO> selectRecycleStuList(BdStudentVO bdStudentVO) {
        BdStudent bdStudent = voToPo(bdStudentVO);
        EntityWrapper<BdStudent> wrapper = new EntityWrapper<>();
        //表示回收站学员
        bdStudent.setIsOrdinary(StudentEnum.RECYCLE_STU.getCode());
        return getPage(bdStudent, bdStudentVO, wrapper);
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param students 数据库对象集合
     * @return List<BdStudentBO> bo对象集合
     */
    public List<BdStudentBO> copyList(List<BdStudent> students) {
        List<BdStudentBO> resultList = new ArrayList<>();
        for (BdStudent source : students) {
            BdStudentBO bdStudentBO = new BdStudentBO();
            BeanUtils.copyBeanProp(bdStudentBO, source);
            resultList.add(bdStudentBO);
        }
        return resultList;
    }

    /**
     * 进行分页
     *
     * @param bdStudent   数据库对象 设置查询参数
     * @param bdStudentVO 入参对象
     * @param wrapper     查询参数
     * @return Page<BdStudentBO> 分页好的出参
     */
    public Page<BdStudentBO> getPage(BdStudent bdStudent, BdStudentVO bdStudentVO, EntityWrapper<BdStudent> wrapper) {
        //模糊查询
        wrapper.setEntity(bdStudent);
        if (!StringUtils.isEmpty(bdStudentVO.getStudentName())) {
            bdStudent.setStudentName(null);
            wrapper.like("student_name", bdStudentVO.getStudentName());
        }
        if (!StringUtils.isEmpty(bdStudentVO.getStudentsPhone())) {
            bdStudent.setStudentsPhone(null);
            wrapper.like("students_phone", bdStudentVO.getStudentsPhone());
        }
        wrapper.orderBy("create_time", false);
        // 添加id倒序，为了避免出现同时间内出现数据自由排序的情况
        wrapper.orderBy("id", false);
        Page<BdStudent> page = this.selectPage(new Page<>(bdStudentVO.getPageNum(), bdStudentVO.getPageSize()), wrapper);
        List<BdStudentBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdStudentBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 新增学生
     *
     * @param bdStudentVO 学生
     * @return 是否新增成功
     */
    @Override
    public boolean insertBdStudent(BdStudentVO bdStudentVO) {
        //判断新增学员的时候，电话号码是否重复（回收站中的学员同样占用手机号，避免同号多学员）
        BdStudent existStudent = selectByPhoneWithRecycle(bdStudentVO.getStudentsPhone());
        if (existStudent != null) {
            throw new CustomException("该学员的电话号码已存在，请重新输入");
        }

        BdStudent bdStudent = voToPo(bdStudentVO);
        //初始化学员信息 默认为在读学员
        bdStudent.setIsOrdinary(StudentEnum.NORMAL_STU.getCode());
        bdStudent.setCreateBy(SecurityUtils.getUserId().toString());
        bdStudent.setCreateTime(new Date());
        //报名时间
        bdStudent.setRegisterTime(new Date());
        boolean result =  this.insert(bdStudent);
        bdStudentVO.setId(bdStudent.getId());
        return result;
    }

    /**
     * 修改学生
     *
     * @param bdStudentVO 学生
     * @return 结果
     */
    @Override
    public boolean updateBdStudent(BdStudentVO bdStudentVO) {
        BdStudent bdStudent = voToPo(bdStudentVO);
        bdStudent.setUpdateTime(DateUtils.getNowDate());
        bdStudent.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdStudent);
    }

    /**
     * 将回收站学员恢复
     *
     * @param id 学生
     * @return boolean
     */
    @Override
    public boolean updateRecoverRec(Long id) {
        BdStudent bdStudent = new BdStudent();
        setPoId(bdStudent);
        bdStudent.setId(id);
        bdStudent.setIsOrdinary(StudentEnum.NORMAL_STU.getCode());
        bdStudent.setUpdateTime(DateUtils.getNowDate());
        bdStudent.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdStudent);
    }


    /**
     * 删除学员 是将学员放入回收站内
     *
     * @param id 学生ID
     * @return 是否将学员放入回收站成功
     */
    @Override
    public boolean deleteBdStudentById(Long id) {
        // 多租户隔离：删除前校验学员归属当前登录机构，防止跨租户越权删除
        BdStudent exist = this.selectById(id);
        if (exist != null) {
            checkStudentBelong(exist);
        }
        //将学生放入回收站中
        BdStudent bdStudent = new BdStudent();
        bdStudent.setIsOrdinary(StudentEnum.RECYCLE_STU.getCode());
        bdStudent.setId(id);
        bdStudent.setUpdateTime(DateUtils.getNowDate());
        bdStudent.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdStudent);
    }

    /**
     * 得到不在该班级的学生
     *
     * @param bdClassVO 班级入参VO
     * @return 不在该班级的学生集合
     */
    @Override
    public List<BdStudentBO> getStudentList(BdClassVO bdClassVO) {
        return studentMapper.getStudentList(bdClassVO);
    }


    /**
     * 查询学员的课次信息列表
     *
     * @param studentId 学员id
     */
    @Override
    public List<CourseInfoStuBO> selectCourseTimeDetailList(Long studentId) {
        return studentMapper.selectCourseTimeDetailList(studentId);
    }

    /**
     * 获取某个老师主教下的学员列表（包括学员班级信息）（加上模糊查询），供学员列表与学员评价等场景复用
     *
     * @param bdStudentVO 学生信息
     * @return 学生信息
     */
    @Override
    public List<BdStudentBO> getStudentDetailByTeacher(BdStudentVO bdStudentVO) {
        return studentMapper.getStudentDetailByTeacher(bdStudentVO);
    }

    /**
     * 在评价学生列表里面得到学生所在的班级
     * @param bdStudentVO 校区id 机构id
     * @return 评价学生列表里面得到学生所在的班级
     */
    @Override
    public List<BdStudentBO> getStudentClassDetail(BdStudentVO bdStudentVO) {
        return studentMapper.getStudentClassDetail(bdStudentVO);
    }

    /**
     * 获得学生集合
     * @param stuIds 学生id集合
     * @return 学生集合
     */
    @Override
    public List<BdStudentBO> getStudentListByIds(Long[] stuIds) {
        return studentMapper.getStudentListByIds(stuIds);
    }

    /**
     * 查询在该手机集合中，是否重复
     * @param phoneSet 手机号集合
     * @return 手机号已存在的学生集合
     */
    @Override
    public List<BdStudent> selectStudentByPhones(TreeSet<String> phoneSet) {
        SysUser sysUser = SecurityUtils.getSysUser();
        EntityWrapper<BdStudent> wrapper = new EntityWrapper<>();
        wrapper.in("students_phone", phoneSet);
        wrapper.eq("mec_id", sysUser.getMecId());
        wrapper.eq("campus_id", sysUser.getCampusId());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        wrapper.eq("is_ordinary", StudentEnum.NORMAL_STU.getCode());
        return this.selectList(wrapper);
    }

    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdStudentVO 入参VO
     * @return BdStudent
     */
    public BdStudent voToPo(BdStudentVO bdStudentVO) {
        BdStudent bdStudent = new BdStudent();
        BeanUtils.copyBeanProp(bdStudent, bdStudentVO);
        SysUser sysUser = SecurityUtils.getSysUser();
        bdStudent.setCampusId(sysUser.getCampusId());
        bdStudent.setMecId(sysUser.getMecId());
        bdStudent.setIsDelete(Constants.NOT_DELETED);
        return bdStudent;
    }


    /**
     * 为入参VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdStudent 入参VO
     * @return BdStudentVO
     */
    /**
     * 学员课时账户导出查询（OS024，C14）：机构/校区由服务端按会话过滤，不受入参控制。
     */
    @Override
    public java.util.List<com.zhenshu.jianxue.mec.educenter.domain.vo.StudentExportVO> selectStudentExportList(
            Long mecId, Long campusId, String studentName) {
        return baseMapper.selectStudentExportList(mecId, campusId, studentName);
    }

    public BdStudent setPoId(BdStudent bdStudent) {
        SysUser sysUser = SecurityUtils.getSysUser();
        bdStudent.setCampusId(sysUser.getCampusId());
        bdStudent.setMecId(sysUser.getMecId());
        bdStudent.setIsDelete(Constants.NOT_DELETED);
        return bdStudent;
    }
}
