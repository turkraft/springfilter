package com.torshid.springfilter.token.matcher.input;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.matcher.Matcher;
import com.torshid.springfilter.token.input.Time;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class TimeMatcher extends Matcher<Time> {

  private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

  @Override
  public Time match(StringBuilder input) {

    ParsePosition position = new ParsePosition(0);

    Date number = formatter.parse(input.toString(), position);

    if (number != null) {
      input.take(position.getIndex());
      return Time.builder().value(number).build();
    }

    return null;

  }

}
