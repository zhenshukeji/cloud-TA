package com.zhenshu.jianxue.mec.base.manager;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.constant.MecConstant;
import com.zhenshu.common.enums.base.CampusPersonEnum;
import com.zhenshu.common.enums.educenter.TeacherEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.exception.user.UserExistException;
import com.zhenshu.common.manager.mec.base.CampusPerWithTeacherManager;
import com.zhenshu.common.manager.mec.base.LoginUserManager;
import com.zhenshu.common.utils.DateUtils;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusPersonnelVO;
import com.zhenshu.jianxue.mec.base.service.IBdCampusPersonnelService;
import com.zhenshu.jianxue.mec.base.service.IBdCampusService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.mapper.domain.educenter.BdTeacher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/6 0006 21:20
 **/
@Service
public class CampusPersonnelInfoManager {

    @Resource
    private IBdCampusPersonnelService campusPersonnelService;
    @Resource
    private IBdCampusService campusService;
    @Resource
    private LoginUserManager loginUserManager;
    @Resource
    private CampusPerWithTeacherManager campusPerWithTeacherManager;

    /**
     * 获取带校区名称的校区人员信息
     */
    public BdCampusPersonnelBO getById(Long id) {
        BdCampusPersonnelBO campusPersonnelBO = campusPersonnelService.selectBdCampusPersonnelById(id);
        BdCampusBO campusBo = campusService.selectBdCampusById(campusPersonnelBO.getCampusId());
        if (campusBo != null) {
            campusPersonnelBO.setCampusName(campusBo.getName());
        }
        return campusPersonnelBO;
    }

    /**
     * 获取带权限id的校区人员详细信息
     */
    public BdCampusPersonnelBO getRoleListById(Long id) {
        BdCampusPersonnelBO campusPersonnelBO = campusPersonnelService.selectBdCampusPersonnelById(id);
        List<Long> roleList = loginUserManager.getRoleListByCampusPersonnel(campusPersonnelBO);
        campusPersonnelBO.setRoleIds(roleList);
        return campusPersonnelBO;
    }

    /**
     * 新增校区人员信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertCampusPersonnel(BdCampusPersonnelVO personnelVO) {
        if (loginUserManager.checkUserExist(personnelVO.getPhone())) {
            throw new UserExistException();
        }
        personnelVO.setType(MecConstant.CAMPUS_PERSONNEL);
        // 新增一个校区人员
        campusPersonnelService.insertBdCampusPersonnel(personnelVO);
        // 新建一个登录人员，同时新增用户id和角色的关联
        loginUserManager.addCampusUser(personnelVO.getPhone(), personnelVO.getContactNumber(), personnelVO.getPassword(), personnelVO.getId(), personnelVO.getRoleIds(), personnelVO.getName());
        // 是否要添加老师
        if (personnelVO.getIsTeacher() != null && CampusPersonEnum.IS_TEACHER.getCode() == personnelVO.getIsTeacher()) {
            if (campusPerWithTeacherManager.checkTeacherExist(personnelVO.getPhone())) {
                throw new CustomException("该电话号码的老师已存在");
            }
            // 老师初始化
            SysUser sysUser = SecurityUtils.getSysUser();
            BdTeacher teacher = new BdTeacher();
            teacher.setCampusId(sysUser.getCampusId());
            teacher.setMecId(sysUser.getMecId());
            teacher.setName(personnelVO.getName());
            teacher.setPhone(personnelVO.getContactNumber());
            teacher.setInductionTime(DateUtils.getNowDate());
            teacher.setIsDelete(Constants.NOT_DELETED);
            teacher.setTaughtNum(TeacherEnum.INITIAL_TAUGHT_NUM.getCode());
            teacher.setVersion(Constants.VERSION);
            teacher.setCreateBy(SecurityUtils.getUserId().toString());
            teacher.setCreateTime(new Date());
            // 老师关联校区人员
            teacher.setAssociationPerson(personnelVO.getId());
            teacher.setPersonType(TeacherEnum.PERSON_CAMPUS.getCode());
            campusPerWithTeacherManager.insertTeacher(teacher);
        }
    }

    /**
     * 更新校区人员信息
     *
     * @param personnelVO 校区人员信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCampusPersonnel(BdCampusPersonnelVO personnelVO) {
        BdCampusPersonnelBO personnelBO = campusPersonnelService.selectBdCampusPersonnelById(personnelVO.getId());
        // 如果改了手机号或者改了负责人名称
        if (!personnelBO.getPhone().equals(personnelVO.getPhone())) {
            // 如果新手机号已存在
            if (loginUserManager.checkUserExist(personnelVO.getPhone())) {
                throw new UserExistException();
            }
        }
        // 更新用户角色
        loginUserManager.updateCampusUser(personnelBO.getPhone(), personnelVO.getPhone(), personnelVO.getContactNumber(), personnelVO.getRoleIds(), personnelVO.getName());
        campusPersonnelService.updateBdCampusPersonnel(personnelVO);
    }


    /**
     * 删除校区人员
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCampusPersonnel(Long campusPersonnelId) {
        BdCampusPersonnelBO personnel = campusPersonnelService.selectBdCampusPersonnelById(campusPersonnelId);
        // 校验该校区人员是否绑定教师
        BdTeacherBO teacherByCampusPerson = campusPerWithTeacherManager.getTeacherByCampusPerson(campusPersonnelId, TeacherEnum.PERSON_CAMPUS.getCode());
        if (teacherByCampusPerson != null) {
            throw new CustomException("该校区人员绑定了老师，不可删除");
        }
        campusPersonnelService.deleteBdCampusPersonnelById(campusPersonnelId);
        // 删除对应的登录人员信息
        loginUserManager.delUser(personnel.getPhone());
    }

    /**
     * 重置密码
     */
    public void resetPwd(Long campusPersonnelId, String pwd) {
        BdCampusPersonnelBO personnel = campusPersonnelService.selectBdCampusPersonnelById(campusPersonnelId);
        loginUserManager.resetPwd(personnel.getPhone(), pwd);
    }


}
