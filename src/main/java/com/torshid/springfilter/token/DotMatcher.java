package com.torshid.springfilter.token;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.Matcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class DotMatcher extends Matcher<Dot> {

  @Override
  public Dot match(StringBuilder input) {

    if (input.index() == '.') {
      return Dot.builder().build();
    }

    return null;

  }

}
