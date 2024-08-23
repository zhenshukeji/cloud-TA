package com.zhenshu.common.annotation;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponses({
        @ApiResponse(code = 200,message = "操作成功"),
        @ApiResponse(code = 400,message="参数列表错误（缺少，格式不匹配)"),
        @ApiResponse(code = 401,message="未授权"),
        @ApiResponse(code = 403,message="访问受限，授权过期"),
        @ApiResponse(code = 404,message="资源，服务未找到")
})
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * @author yuxi
 * @version 1.0
 * @desc 使用自定义注解设置返回状态使用
 **/
public @interface Response {
}
