package com.turkraft.springfilter.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Fields {

  String DEFAULT_PARAMETER_NAME = "fields";

  String parameter() default DEFAULT_PARAMETER_NAME;

  boolean required() default false;

  String defaultValue() default "";

}
