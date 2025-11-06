package com.turkraft.springfilter.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Sort {

  String DEFAULT_PARAMETER_NAME = "sort";

  int DEFAULT_MAX_FIELDS = 10;

  String parameter() default DEFAULT_PARAMETER_NAME;

  boolean required() default false;

  String defaultValue() default "";

  int maxFields() default DEFAULT_MAX_FIELDS;

}
