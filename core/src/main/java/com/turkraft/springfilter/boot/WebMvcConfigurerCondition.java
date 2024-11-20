package com.turkraft.springfilter.boot;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class WebMvcConfigurerCondition implements Condition {

  @Override
  public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    try {
      context
          .getClassLoader()
          .loadClass("org.springframework.web.servlet.config.annotation.WebMvcConfigurer");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

}
