package com.turkraft.springfilter.compiler.token;

import com.turkraft.springfilter.compiler.Extensions;
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
