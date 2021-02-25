package com.turkraft.springfilter.token;

import com.turkraft.springfilter.FilterExtensions;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
public class OperatorMatcher extends LiteralMatcher<Operator> {

  @Override
  public Class<Operator> getEnumClass() {
    return Operator.class;
  }

}
