package com.turkraft.springfilter.boot;

import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.turkraft.springfilter.SpringFilterParameters;

/**
 * Configuration class used to load properties as an alternative to setting the static fields when
 * using Spring.
 */

@Configuration
public class SpringFilterAutoConfig {

  public SpringFilterAutoConfig(
      @Value("${turkraft.springfilter.dateformatter.pattern:#{null}}") String datePattern,
      @Value("${turkraft.springfilter.localdateformatter.pattern:#{null}}") String localDatePattern,
      @Value("${turkraft.springfilter.localdatetimeformatter.pattern:#{null}}") String localDateTimePattern,
      @Value("${turkraft.springfilter.offsetdatetimeformatter.pattern:#{null}}") String offsetDateTimePattern) {

    if (datePattern != null) {
      SpringFilterParameters.DATE_FORMATTER.applyPattern(datePattern);
    }

    if (localDatePattern != null) {
      SpringFilterParameters.LOCALDATE_FORMATTER = DateTimeFormatter.ofPattern(localDatePattern);
    }

    if (localDateTimePattern != null) {
      SpringFilterParameters.LOCALDATETIME_FORMATTER =
          DateTimeFormatter.ofPattern(localDateTimePattern);
    }

    if (offsetDateTimePattern != null) {
      SpringFilterParameters.OFFSETDATETIME_FORMATTER =
          DateTimeFormatter.ofPattern(offsetDateTimePattern);
    }

  }

}
