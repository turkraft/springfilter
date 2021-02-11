package com.torshid.springfilter.token.input;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Text extends Input<String> {

  public static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

  @Override
  public Object getValueAs(Class<?> klass) {

    if (klass.equals(String.class)) {
      return getValue();
    }

    if (klass.isEnum()) {
      for (Object e : klass.getEnumConstants()) {
        if (getValue().equalsIgnoreCase(e.toString())) {
          return e;
        }
      }
      throw new ClassCastException(
          "The value '" + getValue() + "' didn't match any value of enum " + klass.getSimpleName());
    }

    if (klass.equals(Date.class)) {
      Date date = DATE_FORMATTER.parse(getValue(), new ParsePosition(0));
      if (date != null) {
        return date;
      }
      // throw specific exception otherwise, the input was not in the correct format
    }

    throw new ClassCastException("Could not cast input '" + getValue() + "' to class " + klass.getSimpleName());

  }

  @Override
  public String generate() {
    return "'" + getValue() + "'";
  }

}
