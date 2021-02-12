package com.springfilter.token;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.token.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class DotMatcher extends Matcher<Dot> {

  @Override
  public Dot match(StringBuilder input) {

    if (input.index() == '.') {
      input.take();
      return Dot.builder().build();
    }

    return null;

  }

}
