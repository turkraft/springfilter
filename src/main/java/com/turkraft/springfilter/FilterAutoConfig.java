package com.turkraft.springfilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterAutoConfig {

  public FilterAutoConfig(
      @Value("${turkraft.springfilter.dateformatter.pattern:#{null}}") String pattern) {

    if (pattern != null) {
      FilterConfig.DATE_FORMATTER.applyPattern(pattern);
    }

  }

}
