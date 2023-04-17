package com.hspedu.hzxspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Zexi He.
 * @date 2023/4/17 19:04
 * @description:    此注解用于规定返回数据的格式类型
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {

    String value() default "";
}
