package com.biubiu.annotation;

import java.lang.annotation.*;

/**
 * @author 张海彪
 * @create 2019-02-22 18:20
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String value();

}
