package com.turkraft.springfilter.token.input;

import java.text.ParsePosition;

import com.turkraft.springfilter.Extensions;
import com.turkraft.springfilter.FilterConfig;
import com.turkraft.springfilter.token.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class NumeralMatcher extends Matcher<Numeral> {

  @Override
  public Numeral match(StringBuilder input) {

    ParsePosition position = new ParsePosition(0);

    Number number = FilterConfig.NUMBER_FORMAT.parse(input.toString(), position);

    if (number != null) {
      input.take(position.getIndex());
      return Numeral.builder().value(number).build();
    }

    return null;

  }

}
