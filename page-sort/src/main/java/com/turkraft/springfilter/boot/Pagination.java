package com.turkraft.springfilter.boot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Pagination {

  String DEFAULT_PAGE_PARAMETER = "page";

  String DEFAULT_SIZE_PARAMETER = "size";

  String DEFAULT_SORT_PARAMETER = "sort";

  int DEFAULT_PAGE = 0;

  int DEFAULT_SIZE = 20;

  int DEFAULT_MAX_SIZE = 100;

  int DEFAULT_SORT_MAX_FIELDS = 10;

  String pageParameter() default DEFAULT_PAGE_PARAMETER;

  String sizeParameter() default DEFAULT_SIZE_PARAMETER;

  String sortParameter() default DEFAULT_SORT_PARAMETER;

  int defaultPage() default DEFAULT_PAGE;

  int defaultSize() default DEFAULT_SIZE;

  int maxSize() default DEFAULT_MAX_SIZE;

  int sortMaxFields() default DEFAULT_SORT_MAX_FIELDS;

  boolean enableSort() default true;

}
