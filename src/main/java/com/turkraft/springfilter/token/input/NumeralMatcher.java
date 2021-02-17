package com.turkraft.springfilter.token.input;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import com.turkraft.springfilter.Extensions;
import com.turkraft.springfilter.token.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class NumeralMatcher extends Matcher<Numeral> {

  private static final NumberFormat NUMBER_FORMAT;

  static {
    NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    NUMBER_FORMAT.setGroupingUsed(false);
  }

  @Override
  public Numeral match(StringBuilder input) {

    ParsePosition position = new ParsePosition(0);

    Number number = NUMBER_FORMAT.parse(input.toString(), position);

    if (number != null) {
      input.take(position.getIndex());
      return Numeral.builder().value(number).build();
    }

    return null;

  }

}
