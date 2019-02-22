package com.biubiu.annotation;

import java.lang.annotation.*;

/**
 * @author 张海彪
 * @create 2019-02-22 18:19
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    String value();

}
