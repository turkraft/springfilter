package com.turkraft.springfilter.parser;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import com.turkraft.springfilter.FilterParameters;

public class StringConverter {

  public static final NumberFormat NUMBER_FORMAT;

  static {
    NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    NUMBER_FORMAT.setGroupingUsed(false); // in order to not count commas as part of number
  }

  public static Object convert(String input, Class<?> expectedType) {

    input = cleanStringInput(input);

    if (String.class.isAssignableFrom(expectedType)) {
      return input;
    }

    if (char.class.isAssignableFrom(expectedType) || Character.class.isAssignableFrom(expectedType)) {
      if (input.length() != 1) {
        throw new ClassCastException("The input '" + input + "' could not be cast to Char");
      }
      return input.charAt(0);
    }

    if (boolean.class.isAssignableFrom(expectedType) || Boolean.class.isAssignableFrom(expectedType)) {
      return Boolean.valueOf(input);
    }

    if (Date.class.isAssignableFrom(expectedType)) {
      Date date = FilterParameters.DATE_FORMATTER.parse(input, new ParsePosition(0));
      if (date != null) {
        return date;
      }
      throw new ClassCastException("The input '" + input + "' could not be parsed to Date ("
          + FilterParameters.DATE_FORMATTER.toPattern() + ")");
    }

    if (LocalDate.class.isAssignableFrom(expectedType)) {
      try {
        return LocalDate.parse(input, FilterParameters.LOCALDATE_FORMATTER);
      } catch (DateTimeParseException e) {
        throw new ClassCastException("The input '" + input + "' could not be parsed to LocalDate");
      }
    }

    if (LocalDateTime.class.isAssignableFrom(expectedType)) {
      try {
        return LocalDateTime.parse(input, FilterParameters.LOCALDATETIME_FORMATTER);
      } catch (DateTimeParseException e) {
        throw new ClassCastException("The input '" + input + "' could not be parsed to LocalDateTime");
      }
    }

    if (OffsetDateTime.class.isAssignableFrom(expectedType)) {
      try {
        return OffsetDateTime.parse(input, FilterParameters.OFFSETDATETIME_FORMATTER);
      } catch (DateTimeParseException e) {
        throw new ClassCastException("The input '" + input + "' could not be parsed to OffsetDateTime");
      }
    }

    if (LocalTime.class.isAssignableFrom(expectedType)) {
      try {
        return LocalTime.parse(input, FilterParameters.LOCALTIME_FORMATTER);
      } catch (DateTimeParseException e) {
        throw new ClassCastException("The input '" + input + "' could not be parsed to LocalTime");
      }
    }

    if (Instant.class.isAssignableFrom(expectedType)) {
      try {
        return Instant.parse(input);
      } catch (DateTimeParseException e) {
        throw new ClassCastException("The input '" + input + "' could not be parsed to Instant");
      }
    }

    if (UUID.class.isAssignableFrom(expectedType)) {
      try {
        return UUID.fromString(input);
      } catch (IllegalArgumentException e) {
        throw new ClassCastException("The input '" + input + "' could not be parsed to UUID");
      }
    }

    if (expectedType.isEnum()) {
      for (Object e : expectedType.getEnumConstants()) {
        if (input.equalsIgnoreCase(e.toString())) {
          return e;
        }
      }
      throw new ClassCastException(
          "The input '" + input + "' didn't match any enum constant of " + expectedType.getSimpleName());
    }

    if (BigDecimal.class.isAssignableFrom(expectedType)) {
      try {
        return new BigDecimal(input);
      } catch (NumberFormatException ex) {
        throw new ClassCastException("The input '" + input + "' could not be parsed to BigDecimal");
      }
    }

    ParsePosition position = new ParsePosition(0);
    Number number = NUMBER_FORMAT.parse(input, position);

    if (number != null) {

      if (short.class.isAssignableFrom(expectedType) || Short.class.isAssignableFrom(expectedType)) {
        return number.shortValue();
      }

      if (int.class.isAssignableFrom(expectedType) || Integer.class.isAssignableFrom(expectedType)) {
        return number.intValue();
      }

      if (long.class.isAssignableFrom(expectedType) || Long.class.isAssignableFrom(expectedType)) {
        return number.longValue();
      }

      if (float.class.isAssignableFrom(expectedType) || Float.class.isAssignableFrom(expectedType)) {
        return number.floatValue();
      }

      if (double.class.isAssignableFrom(expectedType) || Double.class.isAssignableFrom(expectedType)) {
        return number.doubleValue();
      }

      if (Number.class.isAssignableFrom(expectedType)) {
        return number;
      }

    }

    return null;

  }

  public static String convert(Object input) {

    if (input == null) {
      return null;
    }

    else if (input instanceof BigDecimal) {
      return "'" + input + "'";
    }

    if (input instanceof Boolean || input instanceof Number || input instanceof Character || input instanceof String
        || input instanceof Enum || input instanceof UUID || input.getClass().isPrimitive()) {
      return input.toString();
    }

    else if (input instanceof Date) {
      return FilterParameters.DATE_FORMATTER.format(input);
    }

    else if (input instanceof LocalDate) {
      return FilterParameters.LOCALDATE_FORMATTER.format((LocalDate) input);
    }

    else if (input instanceof LocalDateTime) {
      return FilterParameters.LOCALDATETIME_FORMATTER.format((LocalDateTime) input);
    }

    else if (input instanceof OffsetDateTime) {
      return FilterParameters.OFFSETDATETIME_FORMATTER.format((OffsetDateTime) input);
    }

    else if (input instanceof LocalTime) {
      return FilterParameters.LOCALTIME_FORMATTER.format((LocalTime) input);
    }

    else if (input instanceof Instant) {
      return input.toString();
    }

    return null;

  }

  public static boolean isSupportedAsInput(Object input) {
    return input instanceof Boolean || input instanceof Number || input instanceof Character || input instanceof String
        || input instanceof Enum || input instanceof UUID || input.getClass().isPrimitive() || input instanceof Date
        || input instanceof LocalDate || input instanceof LocalDateTime || input instanceof OffsetDateTime
        || input instanceof LocalTime || input instanceof Instant;
  }

  public static String cleanStringInput(String input) {

    if (input.startsWith("'") && input.endsWith("'")) {
      input = input.substring(1, input.length() - 1);
    }

    input = input.replace("\\'", "'");

    return input;

  }

}
