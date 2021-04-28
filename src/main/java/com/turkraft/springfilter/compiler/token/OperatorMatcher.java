package com.turkraft.springfilter.compiler.token;

import com.turkraft.springfilter.compiler.Extensions;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class OperatorMatcher extends LiteralMatcher<Operator> {

  @Override
  public Class<Operator> getEnumClass() {
    return Operator.class;
  }

}
