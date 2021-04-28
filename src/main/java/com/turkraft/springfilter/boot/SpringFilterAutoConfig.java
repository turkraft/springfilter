package com.turkraft.springfilter.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.turkraft.springfilter.SpringFilterParameters;

@Configuration
public class SpringFilterAutoConfig {

  public SpringFilterAutoConfig(
      @Value("${turkraft.springfilter.dateformatter.pattern:#{null}}") String pattern) {

    if (pattern != null) {
      SpringFilterParameters.DATE_FORMATTER.applyPattern(pattern);
    }

  }

}
