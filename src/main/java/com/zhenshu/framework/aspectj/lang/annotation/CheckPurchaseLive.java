package com.zhenshu.framework.aspectj.lang.annotation;

import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * @author yuxi
 * @version 1.0
 * @date 2020/11/26 9:50
 * @desc Order注解，注解执行的先后顺序
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Order(1)
public @interface CheckPurchaseLive {
}
