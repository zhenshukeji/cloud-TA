package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.CourseTimeEnum;
import com.zhenshu.common.enums.educenter.CourseTimeUpEnum;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdCourseTimeBO;
import com.zhenshu.jianxue.mec.educenter.domain.bo.UpCouInfoStuBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdCourseTimeUpdateVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdCourseTimeUpdateService;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.mapper.domain.educenter.BdCourseTimeUpdate;
import com.zhenshu.mapper.educenter.BdCourseTimeUpdateMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 课时修改记录 Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdCourseTimeUpdateServiceImpl extends ServiceImpl<BdCourseTimeUpdateMapper, BdCourseTimeUpdate> implements IBdCourseTimeUpdateService {

    @Resource
    private BdCourseTimeUpdateMapper bdCourseTimeUpdateMapper;

    /**
     * 查询课时修改记录 列表
     *
     * @param studentId 学生id
     * @return 课时修改记录
     */
    @Override
    public List<UpCouInfoStuBO> selectBdCourseTimeUpdateList(Long studentId) {
        return bdCourseTimeUpdateMapper.selectListByStudentId(studentId);
    }


    /**
     * 新增课时修改记录
     *
     * @param bdCourseTimeUpdateVO 课时修改记录
     * @return 结果
     */
    @Override
    public boolean insertBdCourseTimeUpdate(BdCourseTimeUpdateVO bdCourseTimeUpdateVO) {
        BdCourseTimeUpdate bdCourseTimeUpdate = VOToPO(bdCourseTimeUpdateVO);
        bdCourseTimeUpdate.setCreateTime(DateUtils.getNowDate());
        bdCourseTimeUpdate.setCreateBy(SecurityUtils.getUserId().toString());
        return this.insert(bdCourseTimeUpdate);
    }

    /**
     * 保存点名记录的修改课次记录
     * @param courseTimeBO
     */
    @Override
    public void insertUpdateRecord(BdCourseTimeBO courseTimeBO, CourseTimeUpEnum operation, CourseTimeUpEnum changeToOtherType) {
        BdCourseTimeUpdate courseTimeUpdate = new BdCourseTimeUpdate();
        courseTimeUpdate.setCampusId(courseTimeBO.getCampusId());
        courseTimeUpdate.setMecId(courseTimeBO.getMecId());
        courseTimeUpdate.setChangeType(operation.getCode());
        courseTimeUpdate.setCourseId(courseTimeBO.getCourseId());
        courseTimeUpdate.setStudentId(courseTimeBO.getStudentId());
        courseTimeUpdate.setCourseTimeId(courseTimeBO.getId());
        courseTimeUpdate.setCreateBy(SecurityUtils.getUserId().toString());
        courseTimeUpdate.setCreateTime(new Date());
        courseTimeUpdate.setIsDelete(Constants.NOT_DELETED);
        courseTimeUpdate.setType(changeToOtherType.getCode());
        courseTimeUpdate.setNum(CourseTimeEnum.ONE_COURSE_TIME.getCode());
        courseTimeUpdate.setReason(changeToOtherType.getInfo());
        this.insert(courseTimeUpdate);
    }


    /**
     * 入参VO转换为数据库对象VO 设置机构id，校区id 以及设置为删除的
     *
     * @param bdCourseTimeUpdateVO 入参VO
     * @return BdCourse
     */
    public BdCourseTimeUpdate VOToPO(BdCourseTimeUpdateVO bdCourseTimeUpdateVO) {
        BdCourseTimeUpdate bdCourseTimeUpdate = new BdCourseTimeUpdate();
        BeanUtils.copyBeanProp(bdCourseTimeUpdate, bdCourseTimeUpdateVO);
        SysUser user = SecurityUtils.getSysUser();
        bdCourseTimeUpdate.setCampusId(user.getCampusId());
        bdCourseTimeUpdate.setMecId(user.getMecId());
        bdCourseTimeUpdate.setIsDelete(Constants.NOT_DELETED);
        return bdCourseTimeUpdate;
    }


}
