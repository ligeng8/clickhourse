package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Table {

    /**
     * table 名称,如果没有填写，默认为的model名称
     *
     * @return
     */
    String value() default "";
    String database() default "default";
}
