package com.hspedu.hzxspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Zexi He.
 * @date 2023/4/17 17:18
 * @description:    此注解用于注释方法的形参与请求参数名的关系
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value() default "";
}
