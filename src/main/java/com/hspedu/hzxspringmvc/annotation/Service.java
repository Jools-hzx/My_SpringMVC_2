package com.hspedu.hzxspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Zexi He.
 * @date 2023/4/17 16:05
 * @description:    该注解用于注释 Service 层的 bean 类
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Service {

    String value() default "";
}
