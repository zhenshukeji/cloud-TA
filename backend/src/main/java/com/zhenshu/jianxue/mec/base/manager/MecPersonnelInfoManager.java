package com.zhenshu.jianxue.mec.base.manager;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.constant.MecConstant;
import com.zhenshu.common.constant.UserConstants;
import com.zhenshu.common.enums.educenter.TeacherEnum;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.exception.user.UserExistException;
import com.zhenshu.common.manager.mec.base.LoginUserManager;
import com.zhenshu.common.manager.mec.base.MecPerWithTeacherManager;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusConnectionBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusConnectionVO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMecPersonnelVO;
import com.zhenshu.jianxue.mec.base.service.IBdCampusConnectionService;
import com.zhenshu.jianxue.mec.base.service.IBdMecPersonnelService;
import com.zhenshu.jianxue.mec.educenter.domain.bo.BdTeacherBO;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jing
 * @version 1.0
 * @desc
 * @date 2020/10/5 0005 20:20
 **/
@Service
public class MecPersonnelInfoManager {

    @Resource
    private IBdMecPersonnelService mecPersonnelService;

    @Resource
    private IBdCampusConnectionService campusConnectionService;

    @Resource
    private LoginUserManager loginUserManager;

    @Resource
    private MecPerWithTeacherManager mecPerWithTeacherManager;

    /**
     * 获取机构人员的信息
     *
     * @param mecPersonnelId 机构人员
     */
    public BdMecPersonnelBO getInfo(Long mecPersonnelId) {
        BdMecPersonnelBO mecPersonnelBO = mecPersonnelService.selectBdMecPersonnelById(mecPersonnelId);
        if (mecPersonnelBO == null) {
            throw new CustomException("该账户不存在");
        }
        List<BdCampusConnectionBO> campusConnections = campusConnectionService.selectByMecPersonnelId(mecPersonnelId);
        mecPersonnelBO.setCampusConnectionList(campusConnections);
        return mecPersonnelBO;
    }

    /**
     * 机构人员选择校区登录
     *
     * @param request  请求体，用来拿取token
     * @param campusId 校区id
     * @return 报错信息，如果为空说明成功
     */
    public void login(HttpServletRequest request, Long campusId) {
        loginUserManager.mecPersonnelLogin(request, campusId);
    }


    /**
     * 新增机构人员信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertCampusPersonnel(BdMecPersonnelVO personnelVO) {
        if (loginUserManager.checkUserExist(personnelVO.getPhone())) {
            throw new UserExistException();
        }
        personnelVO.setType(MecConstant.MEC_PERSONNEL);
        // 新增一个机构人员
        mecPersonnelService.insertBdMecPersonnel(personnelVO);
        // 如果有校区权限，那么保存校区权限
        if (personnelVO.getCampusConnectionList() != null && !personnelVO.getCampusConnectionList().isEmpty()) {
            for (BdCampusConnectionVO connectionVO : personnelVO.getCampusConnectionList()) {
                // 设置机构id
                connectionVO.setMecId(personnelVO.getMecId());
                connectionVO.setMecPersonnelId(personnelVO.getId());
                campusConnectionService.insertBdCampusConnection(connectionVO);
            }
        }
        // 新建一个登录人员
        loginUserManager.addUser(personnelVO.getPhone(), personnelVO.getPassword(), personnelVO.getContactNumber(), UserConstants.MEC_PERSONNEL, personnelVO.getId(), personnelVO.getName());
    }

    /**
     * 更新机构人员信息
     *
     * @param personnelVO 机构人员信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCampusPersonnel(BdMecPersonnelVO personnelVO) {
        BdMecPersonnelBO personnelBO = mecPersonnelService.selectBdMecPersonnelById(personnelVO.getId());
        // 如果改了手机号或者改了负责人名称或者修改了联系号码
        if (!personnelBO.getPhone().equals(personnelVO.getPhone()) || !personnelBO.getName().equals(personnelVO.getName()) || !personnelBO.getContactNumber().equals(personnelVO.getContactNumber())) {
            // 如果新手机号已存在
            if (loginUserManager.checkUserExist(personnelVO.getPhone())) {
                throw new UserExistException();
            }
            loginUserManager.updateUser(personnelBO.getPhone(), personnelVO.getPhone(), personnelVO.getContactNumber(), personnelVO.getName());
        }
        updateConnection(personnelVO);
        mecPersonnelService.updateBdMecPersonnel(personnelVO);
    }


    /**
     * 更新和校区之间的联系
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConnection(BdMecPersonnelVO personnelVO) {
        List<BdCampusConnectionBO> campusConnections = campusConnectionService.selectByMecPersonnelId(personnelVO.getId());
        // 旧的校区id列表
        Set<Long> oldCampusIdSet = new HashSet<>();
        for (BdCampusConnectionBO connectionBO : campusConnections) {
            oldCampusIdSet.add(connectionBO.getCampusId());
        }
        // 新的校区id列表
        HashSet<Long> newCampusIdSet = new HashSet<>();
        if (personnelVO.getCampusConnectionList() != null && !personnelVO.getCampusConnectionList().isEmpty()) {
            for (BdCampusConnectionVO connectionVO : personnelVO.getCampusConnectionList()) {
                // 如果是新增的机构关系
                if (!oldCampusIdSet.contains(connectionVO.getCampusId())) {
                    // 查找该机构关系是否已存在，包括已删除的
                    BdCampusConnectionBO bdCampusConnectionBO = campusConnectionService.selectByMecPersonnelIdCampusID(personnelVO.getId(), connectionVO.getCampusId());
                    if (bdCampusConnectionBO != null) {
                        // 重新启动
                        BdCampusConnectionVO bdCampusConnectionVO = new BdCampusConnectionVO();
                        BeanUtils.copyBeanProp(bdCampusConnectionVO, bdCampusConnectionBO);
                        bdCampusConnectionVO.setIsDelete(Constants.NOT_DELETED);
                        campusConnectionService.updateBdCampusConnection(bdCampusConnectionVO);
                    } else {
                        // 设置机构id
                        connectionVO.setMecId(personnelVO.getMecId());
                        connectionVO.setMecPersonnelId(personnelVO.getId());
                        campusConnectionService.insertBdCampusConnection(connectionVO);
                    }
                }
                newCampusIdSet.add(connectionVO.getCampusId());
            }
        }

        // 算出需要删除的连接
        HashSet<Long> delConnection = new HashSet<>(oldCampusIdSet);
        delConnection.removeAll(newCampusIdSet);
        for (BdCampusConnectionBO connectionBO : campusConnections) {
            // 如果存在在删除列表里
            if (delConnection.contains(connectionBO.getCampusId())) {
                // 根据id进行删除
                campusConnectionService.deleteBdCampusConnectionById(connectionBO.getId());
            }
        }

    }

    /**
     * 删除机构人员
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCampusPersonnel(Long campusPersonnelId) {
        BdMecPersonnelBO personnel = mecPersonnelService.selectBdMecPersonnelById(campusPersonnelId);
        // 删除机构人员，校验是否与老师绑定
        BdTeacherBO teacher = mecPerWithTeacherManager.getTeacherByMecPerson(campusPersonnelId, TeacherEnum.PERSON_MEC.getCode());
        if (teacher != null) {
            throw new CustomException("不可删除该机构人员，因为该机构人员绑定了名称为" + teacher.getName() + "老师");
        }
        mecPersonnelService.deleteBdMecPersonnelById(campusPersonnelId);
        // 删除对应的登录人员信息
        loginUserManager.delUser(personnel.getPhone());
        // 删除机构和校区人员之间的关联信息
        List<BdCampusConnectionBO> campusConnections = campusConnectionService.selectByMecPersonnelId(campusPersonnelId);
        for (BdCampusConnectionBO connectionBO : campusConnections) {
            campusConnectionService.deleteBdCampusConnectionById(connectionBO.getId());
        }
    }

    /**
     * 重置密码
     */
    public void resetPwd(Long campusPersonnelId, String pwd) {
        BdMecPersonnelBO personnel = mecPersonnelService.selectBdMecPersonnelById(campusPersonnelId);
        loginUserManager.resetPwd(personnel.getPhone(), pwd);
    }


    /**
     * 根据校区获取机构人员
     */
    public List<BdCampusConnectionBO> getByCampus(BdCampusConnectionVO connectionVO) {
        SysUser sysUser = SecurityUtils.getSysUser();
        connectionVO.setCampusId(sysUser.getCampusId());
        connectionVO.setMecId(sysUser.getMecId());
        // 根据校区id和机构id 和电话号码，查出机构人员在该校区的权限信息
        return campusConnectionService.selectListByCampus(connectionVO);
    }

    /**
     * 根据机构校区关联信息设置权限
     */
    public void setRoles(Long campusConnectionId, Long[] roles) {
        // 根据校区id和机构id 和电话号码，查出机构人员在该校区的权限信息
        loginUserManager.addMecUserRole(roles, campusConnectionId);
    }

    /**
     * 根据机构校区关联信息设置权限
     */
    public List<Long> getRoles(Long campusConnectionId) {
        return loginUserManager.getMecUserRole(campusConnectionId);
    }

}
