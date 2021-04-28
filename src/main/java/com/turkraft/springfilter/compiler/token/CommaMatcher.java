package com.turkraft.springfilter.compiler.token;

import com.turkraft.springfilter.compiler.Extensions;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class CommaMatcher extends Matcher<Comma> {

  @Override
  public Comma match(StringBuilder input) {

    if (input.index() == ',') {
      input.take();
      return Comma.builder().build();
    }

    return null;

  }

}
