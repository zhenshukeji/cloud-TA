package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.CourseEnum;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.common.utils.page.PageUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseService;
import com.zhenshu.mapper.domain.educenter.BdCourse;
import com.zhenshu.mapper.educenter.BdCourseMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.jianxue.mec.statistics.domain.bo.StudentAttendanceBO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 课程Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdCourseServiceImpl extends ServiceImpl<BdCourseMapper, BdCourse> implements IBdCourseService {

    @Resource
    private BdCourseMapper bdCourseMapper;

    /**
     * 查询课程
     *
     * @param id 课程ID
     * @return 课程
     */
    @Override
    public BdCourseBO selectBdCourseById(Long id) {
        BdCourse bdCourse = this.selectById(id);
        if (bdCourse == null) {
            return null;
        }
        return poToBo(bdCourse);
    }

    /**
     * 根据id全局搜索
     *
     * @param courseId 学生ID
     * @return 学生信息
     */
    @Override
    public BdCourseBO selectCourseContainIsDeletedById(Long courseId) {
        BdCourse course = this.selectById(courseId);
        if (course == null) {
            return null;
        }
        return poToBo(course);
    }

    /**
     * 数据库对象转换为出参对象
     *
     * @param bdCourse 数据库对象
     * @return bo出参对象
     */
    public BdCourseBO poToBo(BdCourse bdCourse) {
        BdCourseBO bdCourseBO = new BdCourseBO();
        BeanUtils.copyBeanProp(bdCourseBO, bdCourse);
        return bdCourseBO;
    }

    /**
     * 查询常规课程列表
     *
     * @param bdCourseVO 课程
     * @return 课程
     */
    @Override
    public Page<BdCourseBO> selectConventionalCourseList(BdCourseVO bdCourseVO) {
        BdCourse bdCourse = voToPo(bdCourseVO);
        EntityWrapper<BdCourse> wrapper = new EntityWrapper<>();
        return getPage(bdCourse, bdCourseVO, wrapper, CourseEnum.ORDINARY.getCode());
    }

    /**
     * 查找所有上架的课程
     *
     * @param bdCourseVO 课程
     * @return 课程集合
     */
    @Override
    public Page<BdCourseBO> selectAllCourseByPage(BdCourseVO bdCourseVO) {
        BdCourse bdCourse = voToPo(bdCourseVO);
        bdCourse.setIsShelf(CourseEnum.PUTSHELF.getCode());
        EntityWrapper<BdCourse> wrapper = new EntityWrapper<>();
        return getPage(bdCourse, bdCourseVO, wrapper, bdCourseVO.getIsExperience());
    }

    /**
     * 进行分页
     *
     * @param bdCourse     数据库对象 设置查询参数
     * @param bdCourseVO   入参对象
     * @param wrapper      查询参数
     * @param isExperience 是否为常规课程
     * @return Page<BdCourseBO>分页好的出参
     */
    public Page<BdCourseBO> getPage(BdCourse bdCourse, BdCourseVO bdCourseVO, EntityWrapper<BdCourse> wrapper, Integer isExperience) {
        if (!StringUtils.isEmpty(bdCourse.getCourseName())) {
            wrapper.like("course_name", bdCourse.getCourseName());
        }
        if (bdCourse.getIsShelf() != null) {
            wrapper.eq("is_shelf", bdCourse.getIsShelf());
        }
        wrapper.eq("mec_id", bdCourse.getMecId());
        wrapper.eq("campus_id", bdCourse.getCampusId());
        wrapper.eq("is_delete", bdCourse.getIsDelete());
        if (isExperience != null) {
            wrapper.eq("is_experience", isExperience);
        }
        wrapper.orderBy("create_time", false);
        // 添加id倒序，为了避免出现同时间内出现数据自由排序的情况
        wrapper.orderBy("id", false);
        Page<BdCourse> page = this.selectPage(new Page<>(bdCourseVO.getPageNum(), bdCourseVO.getPageSize()), wrapper);
        List<BdCourseBO> resultList = copyList(page.getRecords());

        // 重新组装page
        return new PageUtils<BdCourseBO>().getPage(resultList, this.selectCount(wrapper));
    }

    /**
     * 将数据库对象转换为BO对象
     *
     * @param bdCourses 数据库对象集合
     * @return List<BdCourseBO> bo对象集合
     */
    public List<BdCourseBO> copyList(List<BdCourse> bdCourses) {
        List<BdCourseBO> resultList = new ArrayList<>();
        for (BdCourse bdCourse : bdCourses) {
            BdCourseBO bdCourseBO = poToBo(bdCourse);
            resultList.add(bdCourseBO);
        }
        return resultList;
    }

    /**
     * 查找所有上架的课程 不分体验还是常规
     *
     * @param bdCourseVO 课程
     * @return 课程集合
     */
    @Override
    public List<BdCourseBO> selectAll(BdCourseVO bdCourseVO) {
        BdCourse bdCourse = voToPo(bdCourseVO);
        bdCourse.setIsShelf(CourseEnum.PUTSHELF.getCode());

        EntityWrapper<BdCourse> wrapper = new EntityWrapper<>();
        wrapper.setEntity(bdCourse);
        return copyList(this.selectList(wrapper));
    }

    /**
     * 查询所有有效且上架的课程
     *
     * @param bdCourseVO 课程类型入参
     * @return 课程列表
     */
    @Override
    public List<BdCourseBO> queryTheCourseOnTheShelves(BdCourseVO bdCourseVO) {
        BdCourse bdCourse = voToPo(bdCourseVO);
        bdCourse.setIsShelf(CourseEnum.PUTSHELF.getCode());
        return this.bdCourseMapper.queryTheCourseOnTheShelves(bdCourse);
    }

    /**
     * 新增课程
     *
     * @param bdCourseVO 课程
     * @return 结果
     */
    @Override
    public boolean insertBdCourse(BdCourseVO bdCourseVO) {
        BdCourse bdCourse = voToPo(bdCourseVO);
        bdCourse.setCreateTime(DateUtils.getNowDate());
        bdCourse.setCreateBy(SecurityUtils.getUserId().toString());
        return this.insert(bdCourse);
    }

    /**
     * 修改课程
     *
     * @param bdCourseVO 课程
     * @return 结果
     */
    @Override
    public boolean updateBdCourse(BdCourseVO bdCourseVO) {
        BdCourse bdCourse = voToPo(bdCourseVO);
        bdCourse.setUpdateTime(DateUtils.getNowDate());
        bdCourse.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdCourse);
    }

    /**
     * 删除课程信息
     *
     * @param id 课程ID
     * @return 结果
     */
    @Override
    public boolean deleteBdCourseById(Long id) {
        BdCourse bdCourse = new BdCourse();
        bdCourse.setIsDelete(Constants.DELETED);
        bdCourse.setId(id);
        return this.updateById(bdCourse);
    }

    /**
     * 学员考勤统计
     */
    @Override
    public List<StudentAttendanceBO> getStudentAttendance() {
        SysUser sysUser = SecurityUtils.getSysUser();
        return bdCourseMapper.getStudentAttendance(sysUser.getMecId(), sysUser.getCampusId());
    }

    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdCourseVO 入参VO
     * @return BdCourse
     */
    public BdCourse voToPo(BdCourseVO bdCourseVO) {
        BdCourse bdCourse = new BdCourse();
        BeanUtils.copyBeanProp(bdCourse, bdCourseVO);
        SysUser user = SecurityUtils.getSysUser();
        bdCourse.setCampusId(user.getCampusId());
        bdCourse.setMecId(user.getMecId());
        bdCourse.setIsDelete(Constants.NOT_DELETED);
        return bdCourse;
    }


}
