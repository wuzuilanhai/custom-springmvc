package com.biubiu.annotation;

import java.lang.annotation.*;

/**
 * @author 张海彪
 * @create 2019-02-22 18:15
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    /**
     * 作用于该类上的注解有一个VALUE属性，说白了就是Controller名称
     */
    String value();

}
