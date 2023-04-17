package com.hspedu.hzxspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Zexi He.
 * @date 2023/4/17 15:07
 * @description:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";
}
