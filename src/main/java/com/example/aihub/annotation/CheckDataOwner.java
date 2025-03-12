package com.example.aihub.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查数据所有者的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheckDataOwner {
    /**
     * 用于查询资源所属用户的服务类
     * @return 服务类
     */
    Class<?> serviceClass();
    /**
     * 从左开始，第几个参数率先具有idParam的参数的索引
     * @return 参数索引
     */
    int index() default 0;
    /**
     * 如果参数是对象，那么从对象中获取id的字段名
     * 资源id的字段名
     * @return 资源id的字段名
     */
    String idField() default "";
}
