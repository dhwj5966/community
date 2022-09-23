package com.starry.community.annotation;

import java.lang.annotation.*;

/**
 * @author Starry
 * @create 2022-09-06-5:24 PM
 * @Describe 该注解用来标识那些需要检查登录状态的handler, 只能用在handler上，
 * 被该注解修饰的handler，必须要用户处于登录状态才可以访问
 */
@Target({ElementType.METHOD})//只能作用在方法上
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Deprecated
public @interface CheckLogin {

}
