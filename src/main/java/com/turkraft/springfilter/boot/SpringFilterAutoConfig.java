package com.turkraft.springfilter.boot;

import com.turkraft.springfilter.FilterParameters;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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
      @Value("${turkraft.springfilter.offsetdatetimeformatter.pattern:#{null}}") String offsetDateTimePattern,
      @Value("${turkraft.springfilter.offsettimeformatter.pattern:#{null}}") String offsetTimePattern,
      @Value("${turkraft.springfilter.zoneddatetimeformatter.pattern:#{null}}") String zonedDateTimePattern,
      @Value("${turkraft.springfilter.localtimeformatter.pattern:#{null}}") String localTimeFormatterPattern,
      @Value("${turkraft.springfilter.yearmonthformatter.pattern:#{null}}") String yearMonthFormatterPattern,
      @Value("${turkraft.springfilter.monthdayformatter.pattern:#{null}}") String monthDayFormatterPattern,
      @Value("${turkraft.springfilter.operator.like.casesensitive:#{null}}") Boolean caseSensitiveLikeOperator) {

    if (datePattern != null) {
      FilterParameters.DATE_FORMATTER = ThreadLocal.withInitial(
          () -> new SimpleDateFormat(datePattern));
    }

    if (localDatePattern != null) {
      FilterParameters.LOCALDATE_FORMATTER = DateTimeFormatter.ofPattern(localDatePattern);
    }

    if (localDateTimePattern != null) {
      FilterParameters.LOCALDATETIME_FORMATTER = DateTimeFormatter.ofPattern(localDateTimePattern);
    }

    if (offsetDateTimePattern != null) {
      FilterParameters.OFFSETDATETIME_FORMATTER =
          DateTimeFormatter.ofPattern(offsetDateTimePattern);
    }

    if (offsetTimePattern != null) {
      FilterParameters.OFFSETTIME_FORMATTER =
          DateTimeFormatter.ofPattern(offsetTimePattern);
    }

    if (zonedDateTimePattern != null) {
      FilterParameters.ZONEDDATETIME_FORMATTER =
          DateTimeFormatter.ofPattern(zonedDateTimePattern);
    }

    if (localTimeFormatterPattern != null) {
      FilterParameters.LOCALTIME_FORMATTER =
          DateTimeFormatter.ofPattern(localTimeFormatterPattern);
    }

    if (yearMonthFormatterPattern != null) {
      FilterParameters.YEARMONTH_FORMATTER =
          DateTimeFormatter.ofPattern(yearMonthFormatterPattern);
    }

    if (monthDayFormatterPattern != null) {
      FilterParameters.MONTHDAY_FORMATTER =
          DateTimeFormatter.ofPattern(monthDayFormatterPattern);
    }

    if (caseSensitiveLikeOperator != null) {
      FilterParameters.CASE_SENSITIVE_LIKE_OPERATOR = caseSensitiveLikeOperator;
    }

  }

}
