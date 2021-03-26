package com.turkraft.springfilter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.function.BiFunction;
import javax.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

  public static SimpleDateFormat DATE_FORMATTER;

  public static NumberFormat NUMBER_FORMAT;

  public static BiFunction<Path<?>, Object, Boolean> FILTERING_AUTHORIZATION;

  public static BiFunction<Path<?>, Object, Boolean> SORTING_AUTHORIZATION;

  static {

    DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    NUMBER_FORMAT.setGroupingUsed(false); // in order to not count commas as part of number

    FILTERING_AUTHORIZATION = null;

    SORTING_AUTHORIZATION = null;

  }

  public FilterConfig(
      @Value("${turkraft.springfilter.dateformatter.pattern:#{null}}") String pattern) {

    if (pattern != null) {
      DATE_FORMATTER.applyPattern(pattern);
    }

  }

}
