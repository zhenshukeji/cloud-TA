package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdStudentClassBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdClassVO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdStudentClassVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdStudentClassService;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.mapper.domain.educenter.BdStudentClass;
import com.zhenshu.mapper.educenter.BdStudentClassMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生班级中间Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdStudentClassServiceImpl extends ServiceImpl<BdStudentClassMapper, BdStudentClass> implements IBdStudentClassService {

    @Resource
    BdStudentClassMapper studentClassMapper;

    /**
     * 查询学生班级中间
     *
     * @param id 学生班级中间ID
     * @return 学生班级中间
     */
    @Override
    public BdStudentClassBO selectBdStudentClassById(Long id) {
        BdStudentClass bdStudentClass = this.selectById(id);
        if (bdStudentClass == null) {
            return null;
        }
        return poToBo(bdStudentClass);
    }

    /**
     * 数据库对象转换为出参对象
     *
     * @param bdStudentClass 数据库对象
     * @return bo出参对象
     */
    public BdStudentClassBO poToBo(BdStudentClass bdStudentClass) {
        BdStudentClassBO bdStudentClassBO = new BdStudentClassBO();
        BeanUtils.copyBeanProp(bdStudentClassBO, bdStudentClass);
        return bdStudentClassBO;
    }

    /**
     * 查询学生班级中间列表
     *
     * @param bdStudentClassVO 学生班级中间
     * @return 学生班级中间
     */
    @Override
    public Page<BdStudentClassBO> selectBdStudentClassList(BdStudentClassVO bdStudentClassVO) {
        BdStudentClass bdStudentClass = VOToPO(bdStudentClassVO);
        EntityWrapper<BdStudentClass> wrapper = new EntityWrapper<>();
        return getPage(bdStudentClass, bdStudentClassVO, wrapper);
    }

    /**
     * 分页查询学生班级中间列表
     *
     * @param classVO 学生班级中间 模糊查询
     * @return 分页学生班级中间
     */
    @Override
    public List<BdStudentBO> selectStudentByClass(BdClassVO classVO) {
        return baseMapper.selectStudentByClass(classVO);
    }

    /**
     * 新增学生班级中间
     *
     * @param bdStudentClassVO 学生班级中间
     * @return 结果
     */
    @Override
    public boolean insertBdStudentClass(BdStudentClassVO bdStudentClassVO) {
        BdStudentClass bdStudentClass = VOToPO(bdStudentClassVO);
        bdStudentClass.setCreateTime(DateUtils.getNowDate());
        bdStudentClass.setHaveTime(DateUtils.getNowDate());
        bdStudentClass.setCreateBy(SecurityUtils.getUserId().toString());
        return this.insert(bdStudentClass);
    }

    /**
     * 修改学生班级中间
     *
     * @param bdStudentClassVO 学生班级中间
     * @return 结果
     */
    @Override
    public boolean updateBdStudentClass(BdStudentClassVO bdStudentClassVO) {
        BdStudentClass bdStudentClass = new BdStudentClass();
        BeanUtils.copyBeanProp(bdStudentClass, bdStudentClassVO);
        bdStudentClass.setUpdateTime(DateUtils.getNowDate());
        EntityWrapper<BdStudentClass> wrapper = new EntityWrapper<>();
        wrapper.eq("id", bdStudentClass.getId());
        return this.update(bdStudentClass, wrapper);
    }


    /**
     * 删除学生班级中间信息
     *
     * @param id 学生班级中间ID
     * @return 结果
     */
    @Override
    public boolean deleteBdStudentClassById(Long id) {
        BdStudentClass bdStudentClass = new BdStudentClass();
        bdStudentClass.setIsDelete(Constants.DELETED);
        bdStudentClass.setId(id);
        return this.updateById(bdStudentClass);
    }

    /**
     * 进行分页
     *
     * @param bdStudentClass   数据库对象 设置查询参数
     * @param bdStudentClassVO 入参对象
     * @param wrapper          查询参数
     * @return Page<BdStudentClassBO>分页好的出参
     */
    public Page<BdStudentClassBO> getPage(BdStudentClass bdStudentClass, BdStudentClassVO bdStudentClassVO, EntityWrapper<BdStudentClass> wrapper) {
        wrapper.setEntity(bdStudentClass);
        Page<BdStudentClass> page = this.selectPage(new Page<>(bdStudentClassVO.getPageNum(), bdStudentClassVO.getPageSize()), wrapper);
        List<BdStudentClassBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdStudentClassBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdStudentClasss 数据库对象集合
     * @return List<BdStudentClassBO> bo对象集合
     */
    public List<BdStudentClassBO> copyList(List<BdStudentClass> bdStudentClasss) {
        List<BdStudentClassBO> resultList = new ArrayList<>();
        for (BdStudentClass source : bdStudentClasss) {
            BdStudentClassBO bdStudentClassBO = new BdStudentClassBO();
            BeanUtils.copyBeanProp(bdStudentClassBO, source);
            resultList.add(bdStudentClassBO);
        }
        return resultList;
    }

    /**
     * 得到所有的学生班级
     *
     * @param bdStudentClassVO 学生班级中间入参VO
     * @return List<BdStudentClassBO>
     */
    @Override
    public List<BdStudentClassBO> getAll(BdStudentClassVO bdStudentClassVO) {
        SysUser sysUser = SecurityUtils.getSysUser();
        bdStudentClassVO.setMecId(sysUser.getMecId());
        bdStudentClassVO.setCampusId(sysUser.getCampusId());
        return studentClassMapper.getStudentsByClassId(bdStudentClassVO);
    }

    /**
     * 根据班级id 进行筛选
     *
     * @param classId 学生班级
     * @return 班级信息列表
     */
    @Override
    public List<BdStudentClassBO> getAllByClassId(Long classId) {
        EntityWrapper<BdStudentClass> wrapper = new EntityWrapper<>();
        wrapper.eq("class_id", classId);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        List<BdStudentClass> bdStudentClassList = this.selectList(wrapper);
        return copyList(bdStudentClassList);
    }

    /**
     * 班级删除学生
     *
     * @param studentClassVO 班级id，学生id
     * @return boolean
     */
    @Override
    public boolean deleteStu(BdStudentClassVO studentClassVO) {
        BdStudentClass bdStudentClass = VOToPO(studentClassVO);
        bdStudentClass.setIsDelete(Constants.DELETED);
        bdStudentClass.setUpdateTime(DateUtils.getNowDate());
        EntityWrapper<BdStudentClass> wrapper = new EntityWrapper<>();
        wrapper.eq("mec_id", bdStudentClass.getMecId());
        wrapper.eq("campus_id", bdStudentClass.getCampusId());
        wrapper.eq("class_id", bdStudentClass.getClassId());
        wrapper.eq("student_id", bdStudentClass.getStudentId());
        return this.update(bdStudentClass, wrapper);
    }

    /**
     * 查看学生是否在该班级
     *
     * @param studentClassVO 学生，班级
     * @return 学生班级信息
     */
    @Override
    public BdStudentClassBO selectByStuCls(BdStudentClassVO studentClassVO) {
        BdStudentClass bdStudentClass = VOToPO(studentClassVO);
        EntityWrapper<BdStudentClass> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdStudentClass);
        bdStudentClass = this.selectOne(wrapper);
        if (bdStudentClass == null) {
            return null;
        }
        BdStudentClassBO BdStudentClassBO = new BdStudentClassBO();
        BeanUtils.copyBeanProp(BdStudentClassBO, bdStudentClass);
        return BdStudentClassBO;
    }

    /**
     * 获得学生的班级
     *
     * @param studentIdList 学生ids
     * @return 学生所在的班级集合
     */
    @Override
    public List<BdStudentClassBO> selectClassByStudentIds(List<Long> studentIdList) {
        return studentClassMapper.selectClassByStudentIds(studentIdList, SecurityUtils.getLoginMecId(), SecurityUtils.getLoginCampusId());
    }

    /**
     * 得到班级里面的学生总数
     *
     * @param classIds 班级ids
     * @return 学生总数
     */
    @Override
    public List<BdStudentClassBO> selectStuCountByClsIds(List<Long> classIds) {
        return studentClassMapper.selectStuCountByClsIds(classIds);
    }

    /**
     * 查询该班级上课的学生（去过滤请假的学生）
     *
     * @param classId      班级id
     * @param clsStartTime 班级上课的开始时间
     * @param clsEndTime   班级上课的结束时间
     * @return 查询该班级上课的学生（去掉请假的学生）
     */
    @Override
    public List<BdStudentClassBO> getStudentByClassIdFilterAskLeave(Long classId, String clsStartTime, String clsEndTime) {
        return studentClassMapper.getStudentByClassIdFilterAskLeave(classId, clsStartTime, clsEndTime);
    }

    /**
     * 获得该班级的所有学生
     *
     * @param classId 班级id
     * @return 所有学生
     */
    @Override
    public List<String> getAllStudentByClassId(Long classId) {
        return studentClassMapper.getAllStudentByClassId(classId);
    }

    /**
     * 获得班级id集合的学生
     *
     * @param classIdList 班级id集合
     * @return 学生集合
     */
    @Override
    public List<BdStudentClass> selectStuByClassIds(List<Long> classIdList) {
        EntityWrapper<BdStudentClass> wrapper = new EntityWrapper<>();
        wrapper.in("class_id", classIdList);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        return this.selectList(wrapper);
    }

    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdStudentClassVO 入参VO
     * @return BdStudent
     */
    public BdStudentClass VOToPO(BdStudentClassVO bdStudentClassVO) {
        BdStudentClass studentClass = new BdStudentClass();
        BeanUtils.copyBeanProp(studentClass, bdStudentClassVO);
        SysUser user = SecurityUtils.getSysUser();
        studentClass.setCampusId(user.getCampusId());
        studentClass.setMecId(user.getMecId());
        studentClass.setIsDelete(Constants.NOT_DELETED);
        return studentClass;
    }


}
