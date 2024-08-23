package com.zhenshu.framework.aspectj.lang.annotation;

import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/26 11:10
 * @desc 会员注解
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Order(1)
public @interface CheckMember {
}
