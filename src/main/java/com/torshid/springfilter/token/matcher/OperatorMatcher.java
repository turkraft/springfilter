package com.torshid.springfilter.token.matcher;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.matcher.LiteralMatcher;
import com.torshid.springfilter.token.Operator;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class OperatorMatcher extends LiteralMatcher<Operator, Operator.Type> {

  @Override
  public Class<Operator.Type> getEnumClass() {
    return Operator.Type.class;
  }

  @Override
  public Operator enumToToken(Enum<Operator.Type> type) {
    return Operator.builder().type((Operator.Type) type).build();
  }

}
