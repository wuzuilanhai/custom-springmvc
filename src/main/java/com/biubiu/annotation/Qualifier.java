package com.biubiu.annotation;

import java.lang.annotation.*;

/**
 * @author 张海彪
 * @create 2019-02-22 18:17
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {

    String value();

}
