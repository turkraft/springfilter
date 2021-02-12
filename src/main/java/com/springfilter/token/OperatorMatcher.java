package com.springfilter.token;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.token.LiteralMatcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class OperatorMatcher extends LiteralMatcher<Operator> {

  @Override
  public Class<Operator> getEnumClass() {
    return Operator.class;
  }

}
