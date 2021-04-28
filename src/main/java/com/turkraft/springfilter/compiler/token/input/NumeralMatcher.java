package com.turkraft.springfilter.compiler.token.input;

import java.text.ParsePosition;
import com.turkraft.springfilter.SpringFilterParameters;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.Matcher;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class NumeralMatcher extends Matcher<Numeral> {

  @Override
  public Numeral match(StringBuilder input) {

    ParsePosition position = new ParsePosition(0);

    Number number = SpringFilterParameters.NUMBER_FORMAT.parse(input.toString(), position);

    if (number != null) {
      input.take(position.getIndex());
      return Numeral.builder().value(number).build();
    }

    return null;

  }

}
