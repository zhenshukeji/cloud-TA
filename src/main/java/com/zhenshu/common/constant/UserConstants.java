package com.zhenshu.common.constant;

/**
 * 用户常量信息
 *
 * @author zhenshu
 */
public class UserConstants {
    /**
     * 平台内系统用户的唯一标志
     */
    public static final String SYS_USER = "SYS_USER";

    /**
     * 正常状态
     */
    public static final String NORMAL = "0";

    /**
     * 异常状态
     */
    public static final String EXCEPTION = "1";

    /**
     * 用户封禁状态
     */
    public static final String USER_DISABLE = "1";

    /**
     * 角色封禁状态
     */
    public static final String ROLE_DISABLE = "1";

    /**
     * 部门正常状态
     */
    public static final String DEPT_NORMAL = "0";

    /**
     * 部门停用状态
     */
    public static final String DEPT_DISABLE = "1";

    /**
     * 字典正常状态
     */
    public static final String DICT_NORMAL = "0";

    /**
     * 是否为系统默认（是）
     */
    public static final String YES = "Y";

    /**
     * 是否菜单外链（是）
     */
    public static final String YES_FRAME = "0";

    /**
     * 是否菜单外链（否）
     */
    public static final String NO_FRAME = "1";

    /**
     * 菜单类型（目录）
     */
    public static final String TYPE_DIR = "M";

    /**
     * 菜单类型（菜单）
     */
    public static final String TYPE_MENU = "C";

    /**
     * 菜单类型（按钮）
     */
    public static final String TYPE_BUTTON = "F";

    /**
     * Layout组件标识
     */
    public final static String LAYOUT = "Layout";

    /**
     * 校验返回结果码
     */
    public final static String UNIQUE = "0";
    public final static String NOT_UNIQUE = "1";

    /**
     * 类型 0机构人员 1校区人员 2平台人员
     */
    public final static String MEC_PERSONNEL = "0";

    /**
     * 类型 0机构人员 1校区人员 2平台人员
     */
    public final static String CAMPUS_PERSONNEL = "1";

    /**
     * 类型 0机构人员 1校区人员 2平台人员
     */
    public final static String PLATFORM_PERSONNEL = "2";


    /**
     * 菜单类型 0机构人员 1校区人员 2平台人员
     */
    public final static int MENU_MEC_TYPE = 0;

    /**
     * 菜单类型 0机构人员 1校区人员 2平台人员
     */
    public final static int MENU_CAMPUS_TYPE = 1;

    /**
     * 菜单类型 0机构人员 1校区人员 2平台人员
     */
    public final static int MENU_PLATFORM_TYPE = 2;

    /**
     * 机构管理员登录状态 0机构管理员 1校区管理员
     */
    public final static String LOGIN_TYPE_MEC = "0";
    public final static String LOGIN_TYPE_CAMPUS = "1";

    /**
     * 用户类型 0是校区人员id  1是机构和校区的关联表id 2平台人员id
     */
    public final static int RU_TYPE_CAMPUS = 0;
    public final static int RU_TYPE_MEC = 1;
    public final static int RU_TYPE_PLATFORM = 2;

    /**
     * 达到某个权限解锁的类型 0机构未审核的时候的菜单
     */
    public final static int RU_UN_AUDIT = 0;


    /**
     * 类型 0校区角色 1平台角色
     */
    public final static int CAMPUS_ROLE = 0;

    public final static int PLATFORM_ROLE = 1;


}
