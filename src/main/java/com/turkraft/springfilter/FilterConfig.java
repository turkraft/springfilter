package com.turkraft.springfilter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FilterConfig {

  public static SimpleDateFormat DATE_FORMATTER;

  public static NumberFormat NUMBER_FORMAT;

  static {

    DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    NUMBER_FORMAT.setGroupingUsed(false); // in order to not count commas as part of number

  }
}
