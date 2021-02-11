package com.torshid.springfilter.token.matcher;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.matcher.LiteralMatcher;
import com.torshid.springfilter.token.Operator;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class OperatorMatcher extends LiteralMatcher<Operator> {

  @Override
  public Class<Operator> getEnumClass() {
    return Operator.class;
  }

}
