package com.zhenshu.jianxue.mec.base.manager;

import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.exception.user.UserExistException;
import com.zhenshu.common.manager.mec.base.LoginUserManager;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdCampusVO;
import com.zhenshu.jianxue.mec.base.service.IBdCampusPersonnelService;
import com.zhenshu.jianxue.mec.base.service.IBdCampusService;
import com.zhenshu.mapper.domain.base.BdCampusPersonnel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author jing
 * @version 1.0
 * @desc 校区管理专用，用于基础模块内部的关联
 * @date 2020/10/9 0009 19:06
 * @note 社区版（OS008）：删除校区虚拟资金账户创建；删除百度地图地址解析（指引 §6.2 排除地图 Bean 与配置）。
 *       经纬度列保留为可空，如需由调用方直接传入，社区版不做解析也不依赖地图凭据。
 **/
@Component
public class CampusInfoManager {

    @Resource
    private IBdCampusService bdCampusService;
    @Resource
    private LoginUserManager loginUserManager;
    @Resource
    private IBdCampusPersonnelService campusPersonnelService;


    /**
     * 更新校区信息
     *
     * @param bdCampusVO 校区信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCampus(BdCampusVO bdCampusVO) {
        BdCampusBO campusBO = bdCampusService.selectBdCampusById(bdCampusVO.getId());
        // 社区版（OS009）：种子里的唯一校区 mgr_name / mgr_phone / mgr_contact_number 为 NULL，
        // 而原写法是 `campusBO.getMgrPhone().equals(...)` —— 对 null 调 equals 直接抛 NPE，
        // 于是「初始化得到的那个校区」永远无法编辑（PUT /base/campus 恒 500）。
        // 两处修正：
        //   1. 用 Objects.equals 做空安全比较；
        //   2. 只有客户端确实提供了新的负责人登录账号（非空）才认为发生「负责人变更」——
        //      空值只表示「未填写」，不构成一次变更，因此不会去改一个不存在的登录账号。
        boolean mgrChanged = !Objects.equals(campusBO.getMgrPhone(), bdCampusVO.getMgrPhone())
                || !Objects.equals(campusBO.getMgrName(), bdCampusVO.getMgrName())
                || !Objects.equals(campusBO.getMgrContactNumber(), bdCampusVO.getMgrContactNumber());
        if (mgrChanged && StringUtils.isNotEmpty(bdCampusVO.getMgrPhone())) {
            // 如果新手机号已存在
            if (!Objects.equals(campusBO.getMgrPhone(), bdCampusVO.getMgrPhone())
                    && loginUserManager.checkUserExist(bdCampusVO.getMgrPhone())) {
                throw new UserExistException();
            }
            if (bdCampusService.checkRepeatMgrPhone(bdCampusVO.getMgrPhone())) {
                throw new CustomException("机构内不能拥有相同校区负责人登录账户");
            }
            loginUserManager.updateUser(campusBO.getMgrPhone(), bdCampusVO.getMgrPhone(), bdCampusVO.getMgrContactNumber(), bdCampusVO.getMgrName());
            // 查到对应的校区人员信息
            BdCampusPersonnel personnel = campusPersonnelService.selectByCampusId(bdCampusVO.getId());
            personnel.setPhone(bdCampusVO.getMgrPhone());
            personnel.setName(bdCampusVO.getMgrName());
            personnel.setContactNumber(bdCampusVO.getMgrContactNumber());
            campusPersonnelService.updateById(personnel);
        }
        bdCampusService.updateBdCampus(bdCampusVO);
    }


    // 社区版（OS009）：删除 insertCampus（新增校区）。
    // 校园创建端点已按指引 §4.2 移除，这里的方法随之成为 0 引用死代码，一并删除 —— 否则
    // 「单机构单校区」边界只靠「上层没有调用方」维持，任何一次重新接线都会把它打破。
    // 唯一校区由 database/002_seed_system.sql 初始化。


    /**
     * 删除校区
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCampus(Long campusId) {
        // 删除校区
        bdCampusService.deleteBdCampusById(campusId);

        // 删除对应的校区人员
        BdCampusPersonnel personnel = campusPersonnelService.selectByCampusId(campusId);
        campusPersonnelService.deleteBdCampusPersonnelById(personnel.getId());

        // 删除对应的登录人员信息
        loginUserManager.delUser(personnel.getPhone());
    }

    /**
     * 重置密码
     */
    public void resetPwd(Long campusId, String pwd) {
        BdCampusPersonnel personnel = campusPersonnelService.selectByCampusId(campusId);
        loginUserManager.resetPwd(personnel.getPhone(), pwd);
    }
}
