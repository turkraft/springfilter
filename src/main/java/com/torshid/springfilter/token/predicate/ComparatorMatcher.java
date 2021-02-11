package com.torshid.springfilter.token.predicate;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.LiteralMatcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ComparatorMatcher extends LiteralMatcher<Comparator> {

  @Override
  public Class<Comparator> getEnumClass() {
    return Comparator.class;
  }

}
