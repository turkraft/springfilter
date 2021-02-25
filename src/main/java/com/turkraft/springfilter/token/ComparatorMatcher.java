package com.turkraft.springfilter.token;

import com.turkraft.springfilter.FilterExtensions;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
public class ComparatorMatcher extends LiteralMatcher<Comparator> {

  @Override
  public Class<Comparator> getEnumClass() {
    return Comparator.class;
  }

}
