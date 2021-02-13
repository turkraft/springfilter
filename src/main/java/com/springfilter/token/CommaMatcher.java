package com.springfilter.token;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.token.Matcher;

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
