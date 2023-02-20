package com.turkraft.springfilter.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Filter {

  String DEFAULT_PARAMETER_NAME = "filter";

  String parameter() default DEFAULT_PARAMETER_NAME;

  Class<?> entityClass() default Void.class;

}
