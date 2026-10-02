package com.zhenshu.jianxue.mec.educenter.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.ClassEnum;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdClassService;
import com.zhenshu.mapper.domain.educenter.BdClass;
import com.zhenshu.mapper.domain.educenter.BdClassRecord;
import com.zhenshu.mapper.educenter.BdClassMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.jianxue.mec.statistics.domain.bo.ClassDetailDataBO;
import com.zhenshu.jianxue.mec.statistics.domain.vo.BdClassDetailVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * 班级Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdClassServiceImpl extends ServiceImpl<BdClassMapper, BdClass> implements IBdClassService {


    @Resource
    private BdClassMapper classMapper;


    /**
     * 查询班级基本信息
     *
     * @param id 班级ID
     * @return 班级
     */
    @Override
    public BdClassBO selectBaseInfoById(Long id) {
        EntityWrapper<BdClass> wrapper = new EntityWrapper<>();
        wrapper.eq("id", id);
        BdClass bdClass = this.selectOne(wrapper);
        if (bdClass == null) {
            return null;
        }
        BdClassBO bdClassBO = new BdClassBO();
        BeanUtils.copyBeanProp(bdClassBO, bdClass);
        return bdClassBO;
    }

    /**
     * 新增班级
     *
     * @param bdClassVO 班级
     */
    @Override
    public void insertBdClass(BdClassVO bdClassVO) {
        // 初始化班级信息
        BdClass bdClass = VOToPO(bdClassVO);
        bdClass.setClassState(ClassEnum.NORMAL_STATUS.getCode());
        bdClass.setHasClass(ClassEnum.INITIAL.getCode());
        bdClass.setHaveClass(ClassEnum.INITIAL.getCode());
        bdClass.setExistStudents(ClassEnum.INITIAL.getCode());
        bdClass.setVersion(Constants.VERSION);
        bdClass.setCreateTime(DateUtils.getNowDate());
        bdClass.setCreateBy(SecurityUtils.getUserId().toString());
        this.insert(bdClass);
        bdClassVO.setId(bdClass.getId());
    }

    /**
     * 废弃班级信息
     *
     * @param id 班级ID
     * @return 结果
     */
    @Override
    public boolean deleteBdClassById(Long id) {
        //不允许删除已有学员的班级，已授课的班级不可废弃
        BdClass bdClass = new BdClass();
        bdClass.setClassState(Constants.DELETED);
        bdClass.setId(id);
        bdClass.setUpdateBy(SecurityUtils.getUserId().toString());
        bdClass.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(bdClass);
    }


    /**
     * 得到主教老师得到的班级名称
     *
     * @param teacherId 教师id
     * @return String
     */
    @Override
    public String getBishopClasses(Long teacherId) {
        BdClass bdClass = new BdClass();
        bdClass.setBishopTeacherId(teacherId);
        bdClass.setClassState(ClassEnum.NORMAL_STATUS.getCode());
        return getClassName(bdClass);
    }

    /**
     * 得到辅助老师得到的班级名称
     *
     * @param teacherId 教师id
     * @return String
     */
    @Override
    public String getHelpClasses(Long teacherId) {
        BdClass bdClass = new BdClass();
        bdClass.setHelpTeacherId(teacherId);
        bdClass.setClassState(ClassEnum.NORMAL_STATUS.getCode());
        return getClassName(bdClass);
    }

    /**
     * 得到辅助老师得到的班级名称
     *
     * @param bdClass 班级
     * @return String
     */
    private String getClassName(BdClass bdClass) {
        EntityWrapper<BdClass> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdClass);
        List<BdClass> list = this.selectList(wrapper);
        StringBuilder builder = new StringBuilder();
        if (list == null || list.size() <= 0) {
            return "";
        }
        for (BdClass source : list) {
            builder.append(source.getClassName()).append(",");
        }
        return builder.substring(0, builder.length() - 1);
    }

    /**
     * 教师id当主教的班级
     *
     * @param bishop 老师Id
     * @return 集合
     */
    @Override
    public Integer getBishopClassCount(Long bishop) {
        EntityWrapper<BdClass> wrapper = new EntityWrapper<>();
        wrapper.eq("bishop_teacher_id", bishop);
        List<BdClass> list = this.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return 0;
        }
        return list.size();
    }

    /**
     * 获得颜色，主教老师名称，辅助老师名称，班级人数
     *
     * @param classIds 班级集合
     * @return 获得颜色，主教老师名称，辅助老师名称，班级人数
     */
    @Override
    public List<BdClassBO> getClassInfoByClassIds(List<Long> classIds) {
        return classMapper.getClassInfoByClassIds(classIds);
    }

    /**
     * 获得其他班级（该校区的班级状态为正常且未被删除的）
     *
     * @param bdClassVO 当前班级id，页码，每页条数
     * @return 其他班级列表
     */
    @Override
    public Page<BdClassBO> selectOtherClass(BdClassVO bdClassVO) {
        EntityWrapper<BdClass> wrapper = new EntityWrapper<>();
        SysUser sysUser = SecurityUtils.getSysUser();
        wrapper.notIn("id", bdClassVO.getId());
        if (!StringUtils.isEmpty(bdClassVO.getClassName())){
            wrapper.like("class_name", bdClassVO.getClassName());
        }
        wrapper.eq("mec_id", sysUser.getMecId());
        wrapper.eq("campus_id", sysUser.getCampusId());
        wrapper.eq("class_state", ClassEnum.NORMAL_STATUS.getCode());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        Page<BdClass> classes = this.selectPage(new Page<>(bdClassVO.getPageNum(), bdClassVO.getPageSize()), wrapper);
        List<BdClassBO> resultList = new ArrayList<>();
        for (BdClass source : classes.getRecords()) {
            BdClassBO bdClassBO = new BdClassBO();
            BeanUtils.copyBeanProp(bdClassBO, source);
            resultList.add(bdClassBO);
        }
        return new PageUtils<BdClassBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 得到所有班级
     *
     * @param bdClassVO 班级入参VO
     * @return List<BdClassBO>
     */
    @Override
    public List<BdClassBO> getAll(BdClassVO bdClassVO) {
        BdClass bdClass = VOToPO(bdClassVO);
        EntityWrapper<BdClass> wrapper = new EntityWrapper<>();
        bdClass.setClassState(ClassEnum.NORMAL_STATUS.getCode());
        wrapper.setEntity(bdClass);
        List<BdClass> classes = this.selectList(wrapper);
        List<BdClassBO> resultList = new ArrayList<>();
        for (BdClass source : classes) {
            BdClassBO bdClassBO = new BdClassBO();
            BeanUtils.copyBeanProp(bdClassBO, source);
            resultList.add(bdClassBO);
        }
        return resultList;
    }

    /**
     * 得到所有班级
     *
     * @param bdClassVO 班级入参VO
     * @return List<BdClassBO>
     */
    @Override
    public Page<BdClassBO> getAllClass(BdClassVO bdClassVO) {
        BdClass bdClass = VOToPO(bdClassVO);
        EntityWrapper<BdClass> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdClass);
        Page<BdClass> classes = this.selectPage(new Page<>(bdClassVO.getPageNum(), bdClassVO.getPageSize()), wrapper);
        List<BdClassBO> resultList = new ArrayList<>();
        for (BdClass source : classes.getRecords()) {
            BdClassBO bdClassBO = new BdClassBO();
            BeanUtils.copyBeanProp(bdClassBO, source);
            resultList.add(bdClassBO);
        }
        return new PageUtils<BdClassBO>().getPage(resultList, this.selectCount(wrapper));
    }


    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdClassVO 入参VO
     * @return bdCallRecord
     */
    public BdClass VOToPO(BdClassVO bdClassVO) {
        BdClass bdClass = new BdClass();
        BeanUtils.copyBeanProp(bdClass, bdClassVO);
        SysUser user = SecurityUtils.getSysUser();
        bdClass.setCampusId(user.getCampusId());
        bdClass.setMecId(user.getMecId());
        bdClass.setIsDelete(Constants.NOT_DELETED);
        return bdClass;
    }


    /**
     * 更新班级的已排课次
     *
     * @param bdClassRecord 上课记录信息
     * @return 是否更新成功
     */
    @Override
    public Boolean updateClassHasClass(BdClassRecord bdClassRecord) {
        Long classId = bdClassRecord.getClassId();
        // 原子自增已授课次，避免并发点名时丢失更新
        return classMapper.incrHasClass(classId) > 0;
    }


    /**
     * 根据id全局搜索
     *
     * @param classId 班级id
     * @return 班级信息
     */
    @Override
    public BdClassBO selectClassDetailById(Long classId) {
        return classMapper.selectClassDetailById(classId);
    }

    /**
     * 根据id 更新
     *
     * @param bdClass 班级信息
     */
    @Override
    public boolean updateClassById(BdClass bdClass) {
        bdClass.setUpdateBy(SecurityUtils.getUserId().toString());
        bdClass.setUpdateTime(new Date());
        return classMapper.updateById(bdClass) > 0;
    }

    /**
     * 获取班级详细信息列表
     *
     * @param bdClassVO 获取课程详细信息列表
     * @return 班级详细信息列表
     */
    @Override
    public List<BdClassBO> getClassDetailedList(BdClassVO bdClassVO) {
        return this.classMapper.getClassList(bdClassVO);
    }

    /**
     * 课表获得所有班级
     *
     * @param bdClassVO 班级名称
     * @return 班级信息
     */
    @Override
    public List<BdClassBO> selectClassBySchoolTimeTable(BdClassVO bdClassVO) {
        bdClassVO.setCampusId(SecurityUtils.getLoginCampusId());
        bdClassVO.setMecId(SecurityUtils.getLoginMecId());
        return this.classMapper.selectClassBySchoolTimeTable(bdClassVO);
    }

    /**
     * 返回老师教授班级的总人数
     *
     * @param teacherIdList 老师id
     * @return 每个老师教授班级的总人数
     */
    @Override
    public Map<Long, Integer> getTotalTaught(List<Long> teacherIdList) {
        List<BdClassBO> list = this.classMapper.getTotalTaught(teacherIdList);

        Map<Long, Integer> map = new HashMap<>();
        for (BdClassBO classBO : list) {
            map.put(classBO.getBishopTeacherId(), classBO.getTaughtStuNum());
        }
        return map;
    }

    /**
     * 获得该课程的班级
     *
     * @param classVO 该课程的班级
     * @return 该课程的班级
     */
    @Override
    public Page<BdClassBO> getClassList(BdClassVO classVO) {
        BdClass bdClass = new BdClass();
        BeanUtils.copyBeanProp(bdClass, classVO);
        EntityWrapper<BdClass> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdClass);
        Page<BdClass> page = this.selectPage(new Page<>(classVO.getPageNum(), classVO.getPageSize()), wrapper);

        List<BdClassBO> resultList = new ArrayList<>();
        for (BdClass bdClass1 : page.getRecords()) {
            BdClassBO classBO = new BdClassBO();
            BeanUtils.copyBeanProp(classBO, bdClass1);
            resultList.add(classBO);
        }
        // 重新组装page
        return new PageUtils<BdClassBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 获取班级明细数据
     *
     * @param bdClassDetailVO 班级
     */
    @Override
    public List<ClassDetailDataBO> getClassData(BdClassDetailVO bdClassDetailVO) {
        SysUser sysUser = SecurityUtils.getSysUser();
        bdClassDetailVO.setCampusId(sysUser.getCampusId());
        bdClassDetailVO.setMecId(sysUser.getMecId());
        return classMapper.getClassData(bdClassDetailVO);
    }

    /**
     * 得到老师所教授的主教班级
     *
     * @param idList 老师ids
     * @return 集合
     */
    @Override
    public List<BdTeacherBO> getClassesByTeacher(List<Long> idList) {
        return classMapper.getClassesByTeacher(idList);
    }

    /**
     * 获得这些班级的学生数量以及班级可容纳数量
     *
     * @param classIds 班级ids
     * @return 学生数量以及班级可容纳数量
     */
    @Override
    public List<BdClassBO> selectStuNumByClsIds(List<Long> classIds) {
        return classMapper.selectStuNumByClsIds(classIds);
    }

}
