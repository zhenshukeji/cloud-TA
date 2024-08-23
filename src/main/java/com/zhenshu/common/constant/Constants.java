package com.zhenshu.common.constant;

import io.jsonwebtoken.Claims;

/**
 * 通用常量信息
 *
 * @author zhenshu
 */
public class Constants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = Claims.SUBJECT;

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";


    /**
     * 是否删除，未删除
     */
    public static final int NOT_DELETED = 0;

    /**
     * 默认的version 值
     */
    public static final int VERSION = 1;


    /**
     * 是否删除，删除
     */
    public static final int DELETED = 1;

    /**
     * 初始值
     */
    public static final int INITIAL_VALUE = 0;

    /**
     * 初始值
     */
    public static final String INITIAL = "0";

    /**
     * 1是
     */
    public static final Integer YES = 1;

    /**
     * 0否
     */
    public static final Integer NO = 0;

    /**
     * 默认页数
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认一页几个
     */
    public static final int DEFALUT_PAGE_SIZE = 10;
    /**
     * 空字符串
     */
    public static final String EMPTY_STR = "";
    /**
     * 平台账户id
     */
    public static final Long PLATFROM_ID = 1L;

    /**
     * 工作日期
     */
    public static final int GROUP_ACT_DAY = 10;

    /**
     * 日期类型为休假
     */
    public static final Integer HOLIDAY = 0;

    /**
     * 日期类型为调休上班
     */
    public static final Integer DAY_WEEK = 1;

    /**
     * 星球六
     */
    public static final int SATURDAY = 6;

    /**
     * 星球天
     */
    public static final int SUNDAY = 7;
}
