package com.zhenshu.jianxue.mec.educenter.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.jianxue.mec.educenter.domain.bo.EnrollmentStatusBO;
import com.zhenshu.jianxue.mec.educenter.domain.vo.EnrollmentStatusVO;
import com.zhenshu.mapper.educenter.BdEnrollmentStatusMapper;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yuxi
 * @version 1.0
 * @desc 报读情况
 * @date
 */
@Service
public class EnrollmentStatusService extends ServiceImpl<BdEnrollmentStatusMapper, EnrollmentStatusVO> {

    @Resource
    private BdEnrollmentStatusMapper enrollmentStatusMapper;

    /**
     * 查询报读情况
     *
     * @param enrollmentVO 报读情况入参
     * @return 报读情况出参
     */
    public List<EnrollmentStatusBO> selectEnrollmentList(EnrollmentStatusVO enrollmentVO) {
        SysUser sysUser = SecurityUtils.getSysUser();
        enrollmentVO.setCampusId(sysUser.getCampusId());
        enrollmentVO.setMecId(sysUser.getMecId());
        return enrollmentStatusMapper.enrollmentList(enrollmentVO);
    }
}
