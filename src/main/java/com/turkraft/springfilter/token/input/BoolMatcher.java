package com.turkraft.springfilter.token.input;

import com.turkraft.springfilter.FilterExtensions;
import com.turkraft.springfilter.token.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
public class BoolMatcher extends Matcher<Bool> {

  @Override
  public Bool match(StringBuilder input) {

    if (input.length() >= 4 && input.substring(0, 4).equalsIgnoreCase("true")) {
      input.take(4);
      return Bool.builder().value(true).build();
    }

    if (input.length() >= 5 && input.substring(0, 5).equalsIgnoreCase("false")) {
      input.take(5);
      return Bool.builder().value(false).build();
    }

    return null;

  }

}
