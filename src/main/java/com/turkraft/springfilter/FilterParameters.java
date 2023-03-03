package com.turkraft.springfilter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class FilterParameters {

  private FilterParameters() {}

  public static SimpleDateFormat DATE_FORMATTER;
  public static DateTimeFormatter LOCALDATE_FORMATTER;
  public static DateTimeFormatter LOCALDATETIME_FORMATTER;
  public static DateTimeFormatter OFFSETDATETIME_FORMATTER;
  public static DateTimeFormatter OFFSETTIME_FORMATTER;
  public static DateTimeFormatter ZONEDDATETIME_FORMATTER;
  public static DateTimeFormatter LOCALTIME_FORMATTER;
  public static DateTimeFormatter YEARMONTH_FORMATTER;
  public static DateTimeFormatter MONTHDAY_FORMATTER;

  public static boolean CASE_SENSITIVE_LIKE_OPERATOR;

  public static FilterFunction[] CUSTOM_FUNCTIONS;

  static {

    DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    LOCALDATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LOCALDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
    OFFSETDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSXXX");
    OFFSETTIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSSXXX");
    ZONEDDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSXXX");
    LOCALTIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    YEARMONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    MONTHDAY_FORMATTER = DateTimeFormatter.ofPattern("--MM-dd");

    CASE_SENSITIVE_LIKE_OPERATOR = false;

    CUSTOM_FUNCTIONS = null;

  }

}
