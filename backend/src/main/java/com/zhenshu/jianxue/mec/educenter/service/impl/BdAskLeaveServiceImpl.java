package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.enums.educenter.AskLeaveEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdAskLeaveBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.BdAskLeaveVO;
import com.zhenshu.jianxue.mec.educenter.service.IBdAskLeaveService;
import com.zhenshu.mapper.domain.educenter.BdAskLeave;
import com.zhenshu.mapper.educenter.BdAskLeaveMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 学生请假Service业务层处理
 *
 * @author yuxi
 * @date 2020-10-04
 */
@Service
public class BdAskLeaveServiceImpl extends ServiceImpl<BdAskLeaveMapper, BdAskLeave> implements IBdAskLeaveService {

    @Resource
    BdAskLeaveMapper askLeaveMapper;


    /**
     * 查询学生请假
     *
     * @param id 学生请假ID
     * @return 学生请假
     */
    @Override
    public BdAskLeaveBO selectBdAskLeaveById(Long id) {
        return askLeaveMapper.selectAskLeaveInfoById(id);
    }

    /**
     * 为入参VO 设置机构id，校区id 以及设置为删除的
     *
     * @param askLeaveVo 入参VO
     */
    public void setVOId(BdAskLeaveVO askLeaveVo) {
        SysUser sysUser = SecurityUtils.getSysUser();
        askLeaveVo.setCampusId(sysUser.getCampusId());
        askLeaveVo.setMecId(sysUser.getMecId());
        askLeaveVo.setIsDelete(Constants.NOT_DELETED);
    }

    /**
     * 查询学生请假列表
     *
     * @param askLeaveVo 学生请假
     * @return 学生请假
     */
    @Override
    public List<BdAskLeaveBO> selectBdAskLeaveList(BdAskLeaveVO askLeaveVo) {
        setVOId(askLeaveVo);
        return askLeaveMapper.selectAskLeaveList(askLeaveVo);
    }


    /**
     * 修改学生请假
     *
     * @param askLeaveVo 学生请假
     * @return 结果
     */
    @Override
    public boolean updateBdAskLeave(BdAskLeaveVO askLeaveVo) {
        // 审核状态流转校验：仅允许对“未审核”的请假进行通过/驳回，且不可回退
        if (askLeaveVo.getType() != null) {
            BdAskLeaveBO existing = askLeaveMapper.selectAskLeaveInfoById(askLeaveVo.getId());
            if (existing != null && existing.getType() != null
                    && existing.getType() != AskLeaveEnum.UN_REVIEW.getCode()) {
                throw new CustomException("该请假已审核，不可重复审核");
            }
        }
        BdAskLeave bdAskLeave = new BdAskLeave();
        BeanUtils.copyBeanProp(bdAskLeave, askLeaveVo);
        // 设置校区id和机构id，逻辑删除状态来过滤数据
        bdAskLeave.setDisposeTime(new Date());
        bdAskLeave.setUpdateTime(DateUtils.getNowDate());
        bdAskLeave.setUpdateBy(SecurityUtils.getUserId().toString());
        return this.updateById(bdAskLeave);
    }

    /**
     * 通过上课时间查找请假的学生
     *
     * @param endInWeek 这周这节课的结束上课时间
     * @return List<BdAskLeaveBO>
     */
    @Override
    public List<BdAskLeaveBO> selectStudentAtTime(String endInWeek, String startInWeek) {
        BdAskLeaveVO askLeaveVo = new BdAskLeaveVO();
        askLeaveVo.setEndInWeek(endInWeek);
        askLeaveVo.setStartInWeek(startInWeek);
        askLeaveVo.setType(AskLeaveEnum.PASS.getCode());
        setVOId(askLeaveVo);
        /**
         * 1、第一种是请假开始时间这节课还没开始
         * 第二种是请假开始时间这节课还没结束，请假结束时间之前结束了
         * 第三种是请假开始的这节课还没结束，请假结束的时候，这节课还没完
         * 2、请假开始时间 < 课程开始时间 < 请假结束时间  ||
         * ( 课程开始时间 > 请假开始时间 &&  课程结束时间 < 请假开始时间 ）
         * 3、第一个判断考虑的是这节课还没开始
         * 第二个判断考虑的是这节课还没结束
         */
        return askLeaveMapper.selectAskLeaveByTime(askLeaveVo);
    }


}
