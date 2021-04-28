package com.turkraft.springfilter.compiler.token;

import com.turkraft.springfilter.compiler.Extensions;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ComparatorMatcher extends LiteralMatcher<Comparator> {

  @Override
  public Class<Comparator> getEnumClass() {
    return Comparator.class;
  }

}
