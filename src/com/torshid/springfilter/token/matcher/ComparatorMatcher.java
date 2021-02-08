package com.torshid.springfilter.token.matcher;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.matcher.LiteralMatcher;
import com.torshid.springfilter.token.Comparator;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ComparatorMatcher extends LiteralMatcher<Comparator, Comparator.Type> {

  @Override
  public Class<Comparator.Type> getEnumClass() {
    return Comparator.Type.class;
  }

  @Override
  public Comparator enumToToken(Enum<Comparator.Type> type) {
    return Comparator.builder().type((Comparator.Type) type).build();
  }

}
