package com.hspedu.hzxspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Zexi He.
 * @date 2023/4/17 16:37
 * @description:    此注解用于完成自动装配
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface AutoWired {

    String value() default "";
}
