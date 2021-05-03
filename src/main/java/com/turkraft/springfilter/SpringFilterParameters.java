package com.turkraft.springfilter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SpringFilterParameters {

  private SpringFilterParameters() {}

  public static SimpleDateFormat DATE_FORMATTER;
  public static DateTimeFormatter LOCALDATE_FORMATTER;
  public static DateTimeFormatter LOCALDATETIME_FORMATTER;
  public static DateTimeFormatter OFFSETDATETIME_FORMATTER;

  public static NumberFormat NUMBER_FORMAT;

  static {

    DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");
    LOCALDATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    LOCALDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
    OFFSETDATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss.SSSXXX");

    NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    NUMBER_FORMAT.setGroupingUsed(false); // in order to not count commas as part of number

  }

}
