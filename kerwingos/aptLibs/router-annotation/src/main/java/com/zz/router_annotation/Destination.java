package com.zz.router_annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)  //注解的生命周期 通常来说，source 类型只会在.java文件保留， class 类型 字节码文件保留， runtime
//运行时都可以使用
public @interface Destination {
    String url();

    String description();
}
