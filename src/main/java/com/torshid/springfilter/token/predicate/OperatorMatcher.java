package com.torshid.springfilter.token.predicate;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.LiteralMatcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class OperatorMatcher extends LiteralMatcher<Operator> {

  @Override
  public Class<Operator> getEnumClass() {
    return Operator.class;
  }

}
