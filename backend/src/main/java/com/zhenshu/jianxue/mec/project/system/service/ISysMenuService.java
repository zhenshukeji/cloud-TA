package com.zhenshu.jianxue.mec.project.system.service;

import com.zhenshu.framework.web.domain.TreeSelect;
import com.zhenshu.jianxue.mec.project.system.domain.SysMenu;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.jianxue.mec.project.system.domain.vo.RouterVo;

import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层
 *
 * @author zhenshu
 */
public interface ISysMenuService {
    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(Long userId);

    /**
     * 根据菜单信息查询系统菜单列表
     *
     * @param menu    菜单
     * @param sysUser
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu, SysUser sysUser);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu   菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @param userType 用户类型
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(Long userId, String userType);

    /**
     * 根据机构类型查询权限
     *
     * @param mecType 类型 0机构权限 1校区权限
     * @return 权限列表
     */
    Set<String> selectMenuByMecType(Integer mecType);

    /**
     * 跟进机构类型和是否审核查询类型
     *
     * @param mecType 类型
     * @param isAudit 是否审核
     * @return 权限列表
     */
    Set<String> selectMenuByMecTypeAndAudit(Integer mecType, Integer isAudit);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据关联id查询菜单,可能是校区id ，也可能是机构id
     *
     * @param userId   用户名称
     * @param userType 角色类型
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId, Integer userType);


    /**
     * 根据mecType查询菜单树信息
     *
     * @param mecType 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByMecType(Integer mecType);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Integer> selectMenuListByRoleId(Long roleId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    String checkMenuNameUnique(SysMenu menu);


    /**
     * 查询机构未审核通过时的菜单列表
     *
     * @param mecType 机构类型
     * @param IsAudit 是否审核通过
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByMecTypeAndIsAudit(int mecType, Integer IsAudit);

}
