package com.turkraft.springfilter.token;

import com.turkraft.springfilter.FilterExtensions;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
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
