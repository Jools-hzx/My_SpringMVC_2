package com.hspedu.hzxspringmvc.annotation;

import java.lang.annotation.*;

/**
 * @author Zexi He.
 * @date 2023/4/17 13:54
 * @description:
 *  该注解用于注释 Controller 层的处理器对象
 *  value 表示该对象存放在 IOC 中的 beanName            
 */

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    String value() default "";
}
