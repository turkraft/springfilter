package com.turkraft.springfilter.compiler.token.input;

import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.Matcher;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
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
