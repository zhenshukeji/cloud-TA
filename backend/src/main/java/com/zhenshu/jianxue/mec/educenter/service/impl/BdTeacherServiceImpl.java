package com.zhenshu.jianxue.mec.educenter.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.TeacherEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdTeacherVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdTeacherService;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.mapper.domain.educenter.BdTeacher;
import com.zhenshu.mapper.educenter.BdTeacherMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 老师Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdTeacherServiceImpl extends ServiceImpl<BdTeacherMapper, BdTeacher> implements IBdTeacherService {

    @Resource
    BdTeacherMapper teacherMapper;

    /**
     * 查询老师基础信息
     *
     * @param id 老师ID
     * @return 老师基础信息
     */
    @Override
    public BdTeacherBO selectBdTeacherById(Long id) {
        BdTeacher bdTeacher = this.selectById(id);
        if (bdTeacher == null) {
            return null;
        }
        return poToBo(bdTeacher);
    }

    /**
     * 根据id全局搜索
     *
     * @param id 老师ID
     * @return 老师
     */
    @Override
    public BdTeacherBO selectTeacherContainIsDeletedById(Long id) {
        BdTeacher bdTeacher = this.selectById(id);
        if (bdTeacher == null) {
            return null;
        }
        return poToBo(bdTeacher);
    }

    /**
     * 数据库对象转换为出参对象
     *
     * @param teacher 数据库对象
     * @return 出参对象
     */
    public BdTeacherBO poToBo(BdTeacher teacher) {
        BdTeacherBO teacherBO = new BdTeacherBO();
        BeanUtils.copyBeanProp(teacherBO, teacher);
        return teacherBO;
    }

    /**
     * 查询老师列表
     *
     * @param bdTeacherVO 老师
     * @return 老师
     */
    @Override
    public Page<BdTeacherBO> selectBdTeacherList(BdTeacherVO bdTeacherVO) {
        BdTeacher bdTeacher = voToPo(bdTeacherVO);
        EntityWrapper<BdTeacher> wrapper = new EntityWrapper<>();
        return getPage(bdTeacher, bdTeacherVO, wrapper);
    }

    /**
     * 进行分页
     *
     * @param bdTeacher   数据库对象 设置查询参数
     * @param bdTeacherVO 入参对象
     * @param wrapper     查询参数
     * @return Page<BdTeacherBO>分页好的出参
     */
    public Page<BdTeacherBO> getPage(BdTeacher bdTeacher, BdTeacherVO bdTeacherVO, EntityWrapper<BdTeacher> wrapper) {
        //模糊查询
        if (!StringUtils.isEmpty(bdTeacherVO.getName())) {
            wrapper.like("name", bdTeacherVO.getName());
        }
        if (bdTeacherVO.getIsDeparture() != null) {
            wrapper.eq("is_departure", bdTeacherVO.getIsDeparture());
        }
        if (bdTeacherVO.getDepartureTime() != null) {
            wrapper.eq("departure_time", bdTeacherVO.getDepartureTime());
        }
        if (bdTeacherVO.getInductionTime() != null) {
            wrapper.eq("induction_time", bdTeacherVO.getInductionTime());
        }

        wrapper.orderBy("is_departure");
        wrapper.orderBy("create_time", false);
        // 添加id倒序，为了避免出现同时间内出现数据自由排序的情况
        wrapper.orderBy("id", false);

        wrapper.eq("mec_id", bdTeacher.getMecId());
        wrapper.eq("campus_id", bdTeacher.getCampusId());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        Page<BdTeacher> page = this.selectPage(new Page<>(bdTeacherVO.getPageNum(), bdTeacherVO.getPageSize()), wrapper);
        List<BdTeacherBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdTeacherBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdTeachers 数据库对象集合
     * @return List<BdTeacherBO> bo对象集合
     */
    public List<BdTeacherBO> copyList(List<BdTeacher> bdTeachers) {
        List<BdTeacherBO> resultList = new ArrayList<>();
        for (BdTeacher bdTeacher : bdTeachers) {
            BdTeacherBO bdTeacherBO = poToBo(bdTeacher);
            if (bdTeacherBO.getIsDeparture() == TeacherEnum.ON_JOB.getCode()) {
                bdTeacherBO.setDepartureTime(null);
            }
            resultList.add(bdTeacherBO);
        }
        return resultList;
    }

    /**
     * 新增老师
     *
     * @param bdTeacherVO 老师
     * @return boolean
     */
    @Override
    public boolean insertBdTeacher(BdTeacherVO bdTeacherVO) {
        if (checkTeacherExist(bdTeacherVO.getPhone())){
            throw new CustomException("该老师的电话号码已存在，请重新输入");
        }
        // 初始化老师
        BdTeacher bdTeacher = voToPo(bdTeacherVO);
        bdTeacher.setVersion(Constants.VERSION);
        bdTeacher.setTaughtNum(Constants.INITIAL_VALUE);
        bdTeacher.setCreateTime(DateUtils.getNowDate());
        bdTeacher.setCreateBy(SecurityUtils.getUserId().toString());
        return this.insert(bdTeacher);
    }

    /**
     * 修改老师
     *
     * @param bdTeacherVO 老师
     * @return 结果
     */
    @Override
    public boolean updateBdTeacher(BdTeacherVO bdTeacherVO) {
        BdTeacher bdTeacher = voToPo(bdTeacherVO);
        bdTeacher.setUpdateTime(DateUtils.getNowDate());
        bdTeacher.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdTeacher);
    }

    /**
     * 删除老师信息
     *
     * @param id 老师ID
     * @return 结果
     */
    @Override
    public boolean deleteBdTeacherById(Long id) {
        BdTeacher bdTeacher = new BdTeacher();
        bdTeacher.setIsDelete(Constants.DELETED);
        bdTeacher.setId(id);
        return this.updateById(bdTeacher);
    }

    /**
     * 修改老师已授班级数
     *
     * @param teacherId 老师id
     * @param type      是增加已授班级数，还是减少已授班级数
     * @return boolean
     */
    @Override
    public boolean updateTeacherTaughtCls(Long teacherId, String type) {
        BdTeacher teacher = this.selectById(teacherId);
        // B-6：教师不存在或 taught_num 为 NULL 时，原实现直接拆箱/解引用抛 NPE，
        // 被全局异常兜底成 500「访问人数过多」，新增/修改班级整条事务回滚且无任何可读提示。
        if (teacher == null) {
            throw new CustomException("未查询到该教师信息，无法更新已授班级数");
        }
        int taughtNum = teacher.getTaughtNum() == null ? 0 : teacher.getTaughtNum();
        if (TeacherEnum.ADD_CLASS_TAUGHT.getMsg().equals(type)) {
            taughtNum += 1;
            BdTeacher update = new BdTeacher();
            update.setTaughtNum(taughtNum);
            update.setId(teacherId);
            return this.updateById(update);
        }
        if (TeacherEnum.REDUCE_CLASS_TAUGHT.getMsg().equals(type)) {
            taughtNum -= 1;
            BdTeacher update = new BdTeacher();
            update.setTaughtNum(taughtNum);
            update.setId(teacherId);
            return this.updateById(update);
        }
        return false;
    }

    /**
     * 查出所有老师关联的校区人员id
     *
     * @return 所有老师关联的校区人员id
     */
    @Override
    public List<Long> selectAllCampusPerson() {
        return teacherMapper.selectAllCampusPerson(SecurityUtils.getLoginMecId(), SecurityUtils.getLoginCampusId());
    }

    /**
     * 查出所有老师关联的机构人员id
     *
     * @return 所有老师关联的机构人员id
     */
    @Override
    public List<Long> selectAllMecPerson() {
        return teacherMapper.selectAllMecPerson(SecurityUtils.getLoginMecId(), SecurityUtils.getLoginCampusId());
    }

    /**
     * 通过机构人员得到老师id
     *
     * @param mecPerson 机构人员id
     * @return 老师
     */
    @Override
    public BdTeacherBO getTeacherByMecPerson(Long mecPerson, Integer personType) {
        EntityWrapper<BdTeacher> wrapper = new EntityWrapper<>();
        wrapper.eq("person_type", personType);
        wrapper.eq("association_person", mecPerson);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        BdTeacher teacher = this.selectOne(wrapper);
        if (teacher == null) {
            return null;
        }
        return poToBo(teacher);
    }

    /**
     * 通过校区人员得到老师id
     *
     * @param campusPerson 校区人员id
     * @return 老师
     */
    @Override
    public BdTeacherBO getTeacherByCampusPerson(Long campusPerson, Integer personType) {
        EntityWrapper<BdTeacher> wrapper = new EntityWrapper<>();
        wrapper.eq("person_type", personType);
        wrapper.eq("association_person", campusPerson);
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        BdTeacher teacher = this.selectOne(wrapper);
        if (teacher == null) {
            return null;
        }
        return poToBo(teacher);
    }

    /**
     * 校验该电话的老师是否存在
     *
     * @param phone 电话
     * @return 是否存在
     */
    @Override
    public boolean checkTeacherExist(String phone) {
        SysUser sysUser = SecurityUtils.getSysUser();
        EntityWrapper<BdTeacher> wrapper = new EntityWrapper<>();
        wrapper.eq("phone", phone);
        wrapper.eq("campus_id", sysUser.getCampusId());
        wrapper.eq("mec_id", sysUser.getMecId());
        wrapper.eq("is_delete", Constants.NOT_DELETED);
        BdTeacher teacher = this.selectOne(wrapper);
        return teacher != null;
    }

    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdTeacherVO 入参VO
     * @return BdTeacher
     */
    public BdTeacher voToPo(BdTeacherVO bdTeacherVO) {
        BdTeacher bdTeacher = new BdTeacher();
        BeanUtils.copyBeanProp(bdTeacher, bdTeacherVO);
        SysUser sysUser = SecurityUtils.getSysUser();
        bdTeacher.setCampusId(sysUser.getCampusId());
        bdTeacher.setMecId(sysUser.getMecId());
        bdTeacher.setIsDelete(Constants.NOT_DELETED);
        return bdTeacher;
    }

}
