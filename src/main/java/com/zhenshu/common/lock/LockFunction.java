package com.zhenshu.common.lock;

import java.lang.annotation.*;

/**
 * @author jing
 * @version 1.0
 * @desc 用于做防重点的锁
 * @date 2021/2/19 0019 10:59
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LockFunction {

    /**
     * 方法名称，用于区分不同的请求
     */
    String methodName();

}
