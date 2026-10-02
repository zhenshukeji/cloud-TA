package com.zhenshu.jianxue.mec.base.manager;

import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.common.constant.MecConstant;
import com.zhenshu.common.constant.UserConstants;
import com.zhenshu.common.enums.UserLoginType;
import com.zhenshu.common.enums.educenter.TeacherEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.manager.mec.base.CampusPerWithTeacherManager;
import com.zhenshu.common.manager.mec.base.MecPerWithTeacherManager;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jing
 * @version 1.0
 * @desc 调用校区人员和机构人员的表。
 * @note 社区版（OS008）：无平台人员角色，平台分支已移除。
 * @date 2020/10/5 0005 15:39
 **/
@Service
public class UserManager {

    @Resource
    private MecPersonnelInfoManager mecPersonnelInfoManager;

    @Resource
    private CampusPersonnelInfoManager campusPersonnelInfoManager;

    @Resource
    private CampusPerWithTeacherManager campusPerWithTeacherManager;

    @Resource
    private MecPerWithTeacherManager mecPerWithTeacherManager;

    /**
     * 把对应的机构人员和校区人员信息设置进去
     *
     * @param user 用户信息
     */
    public void setUserEntity(SysUser user) {
        // 如果是机构人员
        if (user.getType().equals(UserConstants.MEC_PERSONNEL)) {
            setMecPersonnel(user);
            // 如果是校区人员
        } else if (UserConstants.CAMPUS_PERSONNEL.equals(user.getType())) {
            setCampusPersonnel(user);
        } else {
            throw new CustomException("查出来的用户信息类型 type 不正确，非机构人员和校区人员");
        }

    }

    /**
     * 设置机构人员信息
     *
     * @param user 登录用户
     */
    private void setMecPersonnel(SysUser user) {
        BdMecPersonnelBO mecPersonnel = mecPersonnelInfoManager.getInfo(user.getAssociationId());
        if (mecPersonnel == null) {
            throw new CustomException("没有找到登录人员对应的机构人员信息");
        }
        user.setMecPersonnel(mecPersonnel);
        //得到机构人员关联的老师信息
        BdTeacherBO teacher = mecPerWithTeacherManager.getTeacherByMecPerson(mecPersonnel.getId(), TeacherEnum.PERSON_MEC.getCode());
        if (teacher != null) {
            user.setTeacher(teacher);
        }
        // 设置用户类型
        if (mecPersonnel.getType() == MecConstant.MEC_MANAGER) {
            user.setUserType(UserLoginType.TYPE_MEC_MANAGER.getCode());
            // 机构管理员默认进入机构端
            user.setMecMgrLoginType(UserConstants.LOGIN_TYPE_MEC);
        } else if (mecPersonnel.getType() == MecConstant.MEC_PERSONNEL) {
            user.setUserType(UserLoginType.TYPE_MEC_PERSONNEL.getCode());
        }
        // 保存机构id
        user.setMecId(mecPersonnel.getMecId());
    }


    /**
     * 设置校区人员信息
     *
     * @param user 登录用户
     */
    private void setCampusPersonnel(SysUser user) {
        BdCampusPersonnelBO campusPersonnel = campusPersonnelInfoManager.getById(user.getAssociationId());
        if (campusPersonnel == null) {
            throw new CustomException("没有找到登录人员对应的校区人员信息");
        }
        //得到机构人员关联的老师信息
        BdTeacherBO teacher = campusPerWithTeacherManager.getTeacherByCampusPerson(campusPersonnel.getId(), TeacherEnum.PERSON_CAMPUS.getCode());
        if (teacher != null) {
            user.setTeacher(teacher);
        }
        user.setCampusPersonnel(campusPersonnel);
        // 设置用户类型
        if (campusPersonnel.getType() == MecConstant.CAMPUS_MANAGER) {
            user.setUserType(UserLoginType.TYPE_CAMPUS_MANAGER.getCode());
        } else if (campusPersonnel.getType() == MecConstant.CAMPUS_PERSONNEL) {
            user.setUserType(UserLoginType.TYPE_CAMPUS_PERSONNEL.getCode());
        }
        // 保存校区id
        user.setCampusId(campusPersonnel.getCampusId());
        // 保存校区名称
        user.setCampusName(campusPersonnel.getCampusName());
        // 保存机构id
        user.setMecId(campusPersonnel.getMecId());
    }

}
