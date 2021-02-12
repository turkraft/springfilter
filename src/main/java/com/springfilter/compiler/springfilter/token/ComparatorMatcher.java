package com.springfilter.compiler.springfilter.token;

import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.token.LiteralMatcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ComparatorMatcher extends LiteralMatcher<Comparator> {

  @Override
  public Class<Comparator> getEnumClass() {
    return Comparator.class;
  }

}
