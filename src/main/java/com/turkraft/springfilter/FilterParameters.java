package com.turkraft.springfilter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class FilterParameters {

  private FilterParameters() {}

  public static SimpleDateFormat DATE_FORMATTER;
  public static DateTimeFormatter LOCALDATE_FORMATTER;
  public static DateTimeFormatter LOCALDATETIME_FORMATTER;
  public static DateTimeFormatter OFFSETDATETIME_FORMATTER;

  public static boolean CASE_SENSITIVE_LIKE_OPERATOR;

  public static FilterFunction[] CUSTOM_FUNCTIONS;

  static {

    DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    LOCALDATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LOCALDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
    OFFSETDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSXXX");

    CASE_SENSITIVE_LIKE_OPERATOR = false;

    CUSTOM_FUNCTIONS = null;

  }

}
