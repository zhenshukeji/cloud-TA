package com.zhenshu.common.manager.mec.base;

import com.zhenshu.common.constant.Constants;
import com.zhenshu.common.constant.MecConstant;
import com.zhenshu.common.constant.UserConstants;
import com.zhenshu.common.enums.UserLoginType;
import com.zhenshu.common.enums.UserStatus;
import com.zhenshu.common.enums.base.MecEnum;
import com.zhenshu.common.exception.BaseException;
import com.zhenshu.common.exception.CustomException;
import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.common.utils.bean.BeanUtils;
import com.zhenshu.framework.security.LoginUser;
import com.zhenshu.framework.security.service.TokenService;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusConnectionBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdCampusPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMecPersonnelBO;
import com.zhenshu.jianxue.mec.base.domain.bo.BdMechanismBO;
import com.zhenshu.jianxue.mec.base.domain.vo.BdMechanismVO;
import com.zhenshu.jianxue.mec.base.manager.UserManager;
import com.zhenshu.jianxue.mec.base.service.IBdMecPersonnelService;
import com.zhenshu.jianxue.mec.base.service.IBdMechanismService;
import com.zhenshu.jianxue.mec.project.system.domain.SysMenu;
import com.zhenshu.jianxue.mec.project.system.domain.SysRole;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.jianxue.mec.project.system.domain.SysUserRole;
import com.zhenshu.jianxue.mec.project.system.service.ISysMenuService;
import com.zhenshu.jianxue.mec.project.system.service.ISysRoleService;
import com.zhenshu.jianxue.mec.project.system.service.ISysUserRoleService;
import com.zhenshu.jianxue.mec.project.system.service.ISysUserService;
import com.zhenshu.mapper.domain.base.BdMecPersonnel;
import com.zhenshu.mapper.domain.base.BdMechanism;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author jing
 * @version 1.0
 * @desc 跨系统模块和基础模块之间的调用，与登录有关系
 * @date 2020/10/5 0005 15:40
 **/
@Service
public class LoginUserManager {
    private static final Logger log = LoggerFactory.getLogger(LoginUserManager.class);


    @Resource
    private UserManager userManager;

    @Resource
    private ISysUserService userService;

    @Resource
    private IBdMecPersonnelService mecPersonnelService;

    @Resource
    private IBdMechanismService mechanismService;

    @Resource
    private ISysUserRoleService userRoleService;

    @Resource
    private TokenService tokenService;

    @Resource
    private ISysMenuService menuService;

    @Resource
    private ISysRoleService roleService;

    /**
     * 修改机构信息
     *
     * @param bdMechanismVO 机构信息
     */
    public void updateBdMechanism(BdMechanismVO bdMechanismVO) {
        BdMechanismBO bo = mechanismService.selectBdMechanismById(bdMechanismVO.getId());
        if (bo == null) {
            throw new CustomException("机构信息不存在");
        }
        if (!StringUtils.isEmpty(bdMechanismVO.getPhoneNum()) && !bo.getPhoneNum().equals(bdMechanismVO.getPhoneNum())) {
            throw new CustomException("不可直接修改电话号码");
        }
        // 判断管理员名称是否修改了，同步修改名称
        if (bo.getChargeName() != null && !bo.getChargeName().equals(bdMechanismVO.getChargeName())) {
            BdMecPersonnel personnel = mecPersonnelService.selectByMecId(bdMechanismVO.getId());
            personnel.setName(bdMechanismVO.getChargeName());
            mecPersonnelService.updateById(personnel);

            SysUser sysUser = userService.selectUserByUserName(bdMechanismVO.getPhoneNum());
            sysUser.setNickName(bdMechanismVO.getChargeName());
            userService.updateUser(sysUser);
        }
        // 如果是首次提交，更新状态为待审核
        if (MecEnum.WAIT_SUBMIT.getCode() == bo.getStatus()) {
            bdMechanismVO.setStatus(MecEnum.WAIT_REVIEW.getCode());
        } else {
            // 修改封面图片/办学许可证地址/办学许可证编码/营业执照图片地址/营业执照编码
            boolean blMainImg = bdMechanismVO.getMainImg() != null && !bdMechanismVO.getMainImg().equals(bo.getMainImg());
            boolean blLicense = bdMechanismVO.getLicense() != null && !bdMechanismVO.getLicense().equals(bo.getLicense());
            boolean blLicenseCode = bdMechanismVO.getLicenseCode() != null && !bdMechanismVO.getLicenseCode().equals(bo.getLicenseCode());
            boolean blPermit = bdMechanismVO.getPermit() != null && !bdMechanismVO.getPermit().equals(bo.getPermit());
            boolean blPermitCode = bdMechanismVO.getPermitCode() != null && !bdMechanismVO.getPermitCode().equals(bo.getPermitCode());
            boolean blChargeName = bdMechanismVO.getChargeName() != null && !bdMechanismVO.getChargeName().equals(bo.getChargeName());

            if (blMainImg || blLicense || blLicenseCode || blPermit || blPermitCode || blChargeName) {
                bdMechanismVO.setStatus(MecEnum.WAIT_REVIEW.getCode());
            } else {
                if (MecEnum.WAIT_REVIEW.getCode() == bo.getStatus()) {
                    throw new CustomException("未修改，无需提交");
                } else {
                    throw new CustomException("您尚未修改封面图片/办学许可证地址/办学许可证编码/营业执照图片地址/营业执照编码/机构负责人名称中的信息，无需审核");
                }
            }
            bdMechanismVO.setRefuseReason(null);
        }
        mechanismService.updateBdMechanism(bdMechanismVO);

        // 重新查询当前机构管理员的权限
        createLoginUser(SecurityUtils.getSysUser());
    }

    /**
     * 修改机构信息的logo与名称
     *
     * @param bdMechanismVO 机构信息的logo与名称
     */
    public void updateNameOrLogo(BdMechanismVO bdMechanismVO) {
        BdMechanismBO mechanismBO = mechanismService.selectBdMechanismById(bdMechanismVO.getId());
        if (mechanismBO == null) {
            throw new CustomException("机构信息不存在");
        }
        BdMechanism bdMechanism = new BdMechanism();
        bdMechanism.setId(bdMechanismVO.getId());
        if (!StringUtils.isEmpty(bdMechanismVO.getName())) {
            bdMechanism.setName(bdMechanismVO.getName());
        }
        if (!StringUtils.isEmpty(bdMechanismVO.getLogo())) {
            bdMechanism.setLogo(bdMechanismVO.getLogo());
        }
        mechanismService.updateById(bdMechanism);
    }

    public List<SysRole> getRoleList() {
        SysRole role = new SysRole();
        SysUser sysUser = SecurityUtils.getSysUser();
        role.setRoleType(UserConstants.CAMPUS_ROLE);
        role.setMecId(sysUser.getMecId());
        role.setCampusId(sysUser.getCampusId());
        role.setStatus(UserConstants.NORMAL);
        return roleService.selectRoleList(role);
    }


    /**
     * 根据 电话号码进行登录
     *
     * @param username 电话号码
     * @return 用户详细信息
     * @throws UsernameNotFoundException 没有找到用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        } else if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            log.info("登录用户：{} 已被删除.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已被删除");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已停用");
        }
        // 为了兼容之前的登录人员 TODO 以后要改
        if (user.getType() == null) {
            return createLoginUser(user);
        }
        userManager.setUserEntity(user);
        return createLoginUser(user);
    }

    /**
     * 机构人员选择校区登录
     *
     * @param request  请求体，用来拿取token
     * @param campusId 校区id
     */
    public void mecPersonnelLogin(HttpServletRequest request, Long campusId) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        // 登录态失效时给出可读提示，避免直接 NPE 被兜底成 500
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("登录状态已失效，请重新登录");
        }
        SysUser sysUser = loginUser.getUser();
        String userType = sysUser.getUserType();
        // 机构管理员与机构人员都通过 mecPersonnel.campusConnectionList 关联校区；
        // 原实现只放行 TYPE_MEC_PERSONNEL，导致机构管理员切换校区被判为「非机构人员操作」
        boolean isMecSide = UserLoginType.TYPE_MEC_PERSONNEL.getCode().equals(userType)
                || UserLoginType.TYPE_MEC_MANAGER.getCode().equals(userType);
        if (!isMecSide) {
            throw new CustomException("非机构人员操作");
        }
        BdMecPersonnelBO personnel = sysUser.getMecPersonnel();
        List<BdCampusConnectionBO> campusConnectionList = personnel == null ? null : personnel.getCampusConnectionList();
        if (campusConnectionList == null || campusConnectionList.isEmpty()) {
            throw new CustomException("当前账号未关联任何校区，请联系机构负责人分配校区");
        }
        // 查出对应的登录校区
        for (BdCampusConnectionBO campusConnection : campusConnectionList) {
            if (campusId.equals(campusConnection.getCampusId())) {
                sysUser.setCampusId(campusId);
                sysUser.setCampusName(campusConnection.getCampusName());
                sysUser.setCampusConnectionId(campusConnection.getId());
                loginUser.setUser(sysUser);
                // 刷新登录权限列表
                Set<String> permission = getMenuPermission(sysUser);
                // 如果权限列表为空，说明改机构人员没有对应校区的任何权限，那么不允许登录
                if (permission.isEmpty()) {
                    throw new CustomException(String.format(
                            "您还未有该校区的任何功能权限，请联系 %s 的校区负责人分配权限后方可进入校区~", campusConnection.getCampusName()));
                }
                loginUser.setPermissions(permission);
                // 更新登录信息
                tokenService.refreshToken(loginUser);
                return;
            }
        }
        throw new CustomException("此机构人员无该校区的权限");
    }

    /**
     * 创建登录信息，密码比对的步骤在这里，这里会新增一个loginUser，然后获取user.getPassword
     * 也就是数据库里存的加密后的密码，再把传来的密码通过SecurityConfig里配置密码加密逻辑进行比对，判断密码是否正确。
     * 这就是为啥返回的是UserDetails 的原因，因为需要用户名和密码，也可以通过实现PasswordEncoder，注入Bean 来重写密码比对逻辑
     * 记录一下这个逻辑，免得之后又忘了密码比对的地方是在哪里
     *
     * @param user 用户信息
     * @return 用户详细信息
     */
    public UserDetails createLoginUser(SysUser user) {
        Set<String> perms = getMenuPermission(user);
        return new LoginUser(user, perms);
    }


    /**
     * 获取权限列表
     *
     * @return 权限列表
     */
    public Set<String> getMenuPermission(SysUser user) {
        Set<String> perms = new HashSet<>();
        // 管理员拥有所有权限
        /*
         * 1.如果是机构管理员，拥有所有权限
         * 2.如果是平台管理员，拥有所有权限
         * 3.如果是校区管理员，拥有校区的所有权限
         * 4.如果是校区人员，拥有userId的所有权限
         * 5.如果是机构人员，拥有connect Id的所有权限
         * 6.如果是平台人员，具有userId的所有权限
         */
        if (user.isAdmin()) {
            perms.add("*:*:*");
        }
        if (UserLoginType.TYPE_MEC_MANAGER.getCode().equals(user.getUserType())) {
            Long mecId = user.getMecId();
            BdMechanismBO bo = mechanismService.selectBdMechanismById(mecId);
            // 如果没有审核通过
            if (bo.getStatus() != MecEnum.PASSED.getCode()) {
                perms.addAll(menuService.selectMenuByMecTypeAndAudit(UserConstants.MENU_MEC_TYPE, Constants.NO));
            } else {
                perms.add("*:*:*");
            }
        }
        if (UserLoginType.TYPE_CAMPUS_MANAGER.getCode().equals(user.getUserType())) {
            perms.addAll(menuService.selectMenuByMecType(UserConstants.MENU_CAMPUS_TYPE));
        }
        if (UserLoginType.TYPE_CAMPUS_PERSONNEL.getCode().equals(user.getUserType())) {
            perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId(), String.valueOf(UserConstants.RU_TYPE_CAMPUS)));
        }
        if (UserLoginType.TYPE_MEC_PERSONNEL.getCode().equals(user.getUserType())) {
            perms.addAll(menuService.selectMenuPermsByUserId(user.getCampusConnectionId(), String.valueOf(UserConstants.RU_TYPE_MEC)));
        }
        return perms;
    }


    /**
     * 根据用户信息获取菜单列表
     *
     * @param user 登录用户信息
     * @return 菜单列表
     */
    public List<SysMenu> selectMenuTreeByUser(SysUser user) {
        Long userId = user.getUserId();
        List<SysMenu> menus = null;
        if (user.getUserType() == null) {
            menus = menuService.selectMenuTreeByUserId(userId);
            // 1.如果是校区人员，那么根据userId查询
            // 2.如果是机构人员，那么根据connectId查询
            // 3.如果是校区管理员，查出校区相关的菜单
            // 4.如果是机构管理员，判断是否为进入校区的状态，如果未进入校区，查出机构的菜单，如果进入校区，相当于是以校区管理员的身份进入，那么查出校区相关的菜单
            // 5.如果是平台管理员，判读是否为进入机构的状态，如果
        } else {
            // 校区人员
            if (UserLoginType.TYPE_CAMPUS_PERSONNEL.getCode().equals(user.getUserType())) {
                menus = menuService.selectMenuTreeByUserId(userId, UserConstants.RU_TYPE_CAMPUS);
                // 机构人员
            } else if (UserLoginType.TYPE_MEC_PERSONNEL.getCode().equals(user.getUserType())) {
                menus = menuService.selectMenuTreeByUserId(user.getCampusConnectionId(), UserConstants.RU_TYPE_MEC);
                // 校区管理员
            } else if (UserLoginType.TYPE_CAMPUS_MANAGER.getCode().equals(user.getUserType())) {
                menus = menuService.selectMenuTreeByMecType(UserConstants.MENU_CAMPUS_TYPE);
                // 机构管理员
            } else if (UserLoginType.TYPE_MEC_MANAGER.getCode().equals(user.getUserType())) {
                menus = getSysMenusByMec(user);
            }
        }
        return menus;
    }
    /**
     * 获取机构的路由
     *
     * @param user 用户信息
     * @return 路由信息
     */
    private List<SysMenu> getSysMenusByMec(SysUser user) {
        List<SysMenu> menus;// 如果是以校区管理员登录
        if (UserConstants.LOGIN_TYPE_CAMPUS.equals(user.getMecMgrLoginType())) {
            menus = menuService.selectMenuTreeByMecType(UserConstants.MENU_CAMPUS_TYPE);
        } else if (UserConstants.LOGIN_TYPE_MEC.equals(user.getMecMgrLoginType())) {
            Long mecId = user.getMecId();
            BdMechanismBO bo = mechanismService.selectBdMechanismById(mecId);
            if (bo.getStatus() != MecEnum.PASSED.getCode()) {
                menus = menuService.selectMenuTreeByMecTypeAndIsAudit(UserConstants.MENU_MEC_TYPE, Constants.NO);
            } else {
                menus = menuService.selectMenuTreeByMecType(UserConstants.MENU_MEC_TYPE);
            }
        } else {
            throw new CustomException("机构管理员登录状态不正确");
        }
        return menus;
    }


    /**
     * 校验该电话号码是否已存在 sysUser里
     *
     * @param phone    电话号码
     * @param passWord 密码
     * @return 系统用户登录类
     */
    public SysUser checkUser(String phone, String passWord) {
        SysUser user = new SysUser();
        user.setUserName(phone);
        user.setPhonenumber(phone);
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return null;
        }
        user.setPassword(SecurityUtils.encryptPassword(passWord));
        return user;
    }

    /**
     * 保存机构管理员信息
     *
     * @param user        用户信息
     * @param mechanismBO 机构信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMecUser(SysUser user, BdMechanismBO mechanismBO) {
        // 新增一个机构管理员信息
        BdMecPersonnel mecPersonnel = new BdMecPersonnel();
        mecPersonnel.setMecId(mechanismBO.getId());
        mecPersonnel.setIsDelete(Constants.NOT_DELETED);
        mecPersonnel.setPhone(mechanismBO.getPhoneNum());
        mecPersonnel.setType(MecConstant.MEC_MANAGER);
        mecPersonnel.setName(MecConstant.MEC_DEFAULT_NAME);
        mecPersonnel.setCreateTime(new Date());

        // 保存到数据库
        mecPersonnelService.insert(mecPersonnel);

        //保存用户信息
        user.setNickName(MecConstant.MEC_DEFAULT_NAME);
        user.setType(UserConstants.MEC_PERSONNEL);
        user.setAssociationId(mecPersonnel.getId());
        userService.insertUser(user);

        // 保存用户部分信息，用于登录
        BdMecPersonnelBO mecPersonnelBO = new BdMecPersonnelBO();
        BeanUtils.copyBeanProp(mecPersonnel, mecPersonnelBO);
        user.setMecPersonnel(mecPersonnelBO);
        user.setUserType(UserLoginType.TYPE_MEC_MANAGER.getCode());

    }

    /**
     * 新增用户信息
     *
     * @param phone         电话号码
     * @param passWord      密码
     * @param type          用户类型
     * @param associationId 关联id
     */
    public void addUser(String phone, String passWord, String concatNumber, String type, Long associationId, String name) {
        SysUser sysUser = initUser(phone, passWord, concatNumber, type, associationId, name);
        userService.insertUser(sysUser);
    }

    /**
     * 新增校区用户包括角色
     *
     * @param phone         电话号码
     * @param passWord      密码
     * @param associationId 关联id
     */
    public void addCampusUser(String phone, String concatNumber, String passWord, Long associationId, Long[] roleIds, String name) {
        SysUser sysUser = initUser(phone, passWord, concatNumber, UserConstants.CAMPUS_PERSONNEL, associationId, name);
        sysUser.setRoleIds(roleIds);
        userService.insertUser(sysUser, UserConstants.RU_TYPE_CAMPUS);
    }

    /**
     * 初始化用户信息
     */
    private SysUser initUser(String phone, String passWord, String concatNumber, String type, Long associationId, String name) {
        SysUser sysUser = new SysUser();
        sysUser.setPassword(SecurityUtils.encryptPassword(passWord));
        sysUser.setPhonenumber(concatNumber);
        sysUser.setEmail(phone);
        sysUser.setUserName(phone);
        sysUser.setType(type);
        sysUser.setNickName(name);
        sysUser.setAssociationId(associationId);
        sysUser.setCreateTime(new Date());
        return sysUser;
    }


    /**
     * 修改用户信息
     *
     * @param beforePhone 之前的电话号码
     * @param changePhone 之后的电话号码
     */
    public void updateUser(String beforePhone, String changePhone, String contactNumber, String name) {
        SysUser sysUser = userService.selectUserByUserName(beforePhone);
        sysUser.setPhonenumber(contactNumber);
        sysUser.setNickName(name);
        sysUser.setUserName(changePhone);
        sysUser.setEmail(changePhone);
        userService.updateUser(sysUser);
    }

    /**
     * 修改用户信息
     *
     * @param beforePhone 之前的电话号码
     * @param changePhone 之后的电话号码
     */
    public void updateCampusUser(String beforePhone, String changePhone, String contactNumber, Long[] roleIds, String name) {
        SysUser sysUser = userService.selectUserByUserName(beforePhone);
        sysUser.setPhonenumber(contactNumber);
        sysUser.setUserName(changePhone);
        sysUser.setEmail(changePhone);
        sysUser.setNickName(name);
        sysUser.setRoleIds(roleIds);
        userService.updateUser(sysUser, UserConstants.RU_TYPE_CAMPUS);
    }

    /**
     * 获取校区人员的权限列表
     */
    public List<Long> getRoleListByCampusPersonnel(BdCampusPersonnelBO campusPersonnelBO) {
        SysUser sysUser = userService.selectUserByUserName(campusPersonnelBO.getPhone());
        return userRoleService.selectByUserIdAndUserType(sysUser.getUserId(), UserConstants.RU_TYPE_CAMPUS);
    }

    /**
     * 删除用户信息
     */
    public void delUser(String phone) {
        SysUser sysUser = userService.selectUserByUserName(phone);
        userService.deleteUserById(sysUser.getUserId());
    }

    /**
     * 重置密码
     *
     * @param phone 手机号
     * @param pwd   密码
     */
    public void resetPwd(String phone, String pwd) {
        SysUser user = userService.selectUserByUserName(phone);
        user.setPassword(SecurityUtils.encryptPassword(pwd));
        user.setUpdateBy(SecurityUtils.getUsername());
        userService.resetPwd(user);
    }


    /**
     * 校验用户是否已经存在
     *
     * @param phone 电话号码
     */
    public boolean checkUserExist(String phone) {
        SysUser user = userService.selectUserByUserName(phone);
        return user != null;
    }


    /**
     * 更换机构负责人手机号和登录密码
     *
     * @param mecId      机构id
     * @param chargeName 机构负责人昵称
     * @param phone      电话号码
     * @param passWord   密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void change(Long mecId, String phone, String passWord, String chargeName) {
        // 查到机构信息，更换机构手机号
        BdMechanismBO mechanismBO = mechanismService.selectBdMechanismById(mecId);
        // 保存上一个的电话号码
        String lastPhone = mechanismBO.getPhoneNum();
        BdMechanism mechanism = new BdMechanism();
        BeanUtils.copyBeanProp(mechanism, mechanismBO);
        mechanism.setPhoneNum(phone);
        mechanism.setChargeName(chargeName);
        mechanismService.updateById(mechanism);

        // 查询到机构负责人信息
        BdMecPersonnel personnel = mecPersonnelService.selectByMecId(mecId);
        personnel.setPhone(phone);
        personnel.setName(chargeName);
        mecPersonnelService.updateById(personnel);

        // 查到对应的用户信息
        SysUser sysUser = userService.selectUserByUserName(lastPhone);
        sysUser.setUserName(phone);
        sysUser.setPhonenumber(phone);
        sysUser.setEmail(phone);
        sysUser.setNickName(chargeName);
        sysUser.setPassword(SecurityUtils.encryptPassword(passWord));
        userService.updateUser(sysUser);
    }

    /**
     * 新增机构人员角色关联
     */
    @Transactional(rollbackFor = Exception.class)
    public void addMecUserRole(Long[] roleIds, Long mecPersonnelConnectId) {
        userRoleService.deleteUserRoleByUserId(mecPersonnelConnectId, UserConstants.RU_TYPE_MEC);
        if (StringUtils.isNotNull(roleIds)) {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(mecPersonnelConnectId);
                ur.setUserType(UserConstants.RU_TYPE_MEC);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0) {
                userRoleService.batchUserRole(list);
            }
        }
    }


    /**
     * 获取机构人员角色列表
     */
    public List<Long> getMecUserRole(Long mecPersonnelConnectId) {
        return userRoleService.selectByUserIdAndUserType(mecPersonnelConnectId, UserConstants.RU_TYPE_MEC);
    }


    /**
     * 机构管理员进入校区
     */
    public void intoCampus(LoginUser loginUser, SysUser sysUser, Long campusId) {
        sysUser.setCampusId(campusId);
        // 以校区的身份登录
        if (sysUser.getUserType().equals(UserLoginType.TYPE_MEC_MANAGER.getCode())) {
            sysUser.setMecMgrLoginType(UserConstants.LOGIN_TYPE_CAMPUS);
        } else {
            sysUser.setPlatformLoginType(UserLoginType.LT_CAMPUS_MANAGER.getCode());
        }
        loginUser.setUser(sysUser);
        tokenService.refreshToken(loginUser);
    }

    /**
     * 机构管理员返回机构
     */
    public void goBackMec(LoginUser loginUser, SysUser sysUser) {
        sysUser.setCampusId(null);
        if (sysUser.getUserType().equals(UserLoginType.TYPE_MEC_MANAGER.getCode())) {
            sysUser.setMecMgrLoginType(UserConstants.LOGIN_TYPE_MEC);
        } else {
            sysUser.setPlatformLoginType(UserLoginType.LT_MEC_MANAGER.getCode());
        }
        loginUser.setUser(sysUser);
        tokenService.refreshToken(loginUser);
    }
}
