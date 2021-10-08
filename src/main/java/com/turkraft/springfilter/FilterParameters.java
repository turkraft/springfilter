package com.turkraft.springfilter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class FilterParameters {

  private FilterParameters() {}

  public static SimpleDateFormat DATE_FORMATTER;
  public static DateTimeFormatter LOCALDATE_FORMATTER;
  public static DateTimeFormatter LOCALDATETIME_FORMATTER;
  public static DateTimeFormatter OFFSETDATETIME_FORMATTER;

  static {

    DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    LOCALDATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LOCALDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
    OFFSETDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSXXX");

  }

}
