package com.turkraft.springfilter.token;

import com.turkraft.springfilter.Extensions;

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
