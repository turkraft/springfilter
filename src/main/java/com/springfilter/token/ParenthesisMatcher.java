package com.springfilter.token;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.token.Matcher;
import com.springfilter.token.Parenthesis.Type;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ParenthesisMatcher extends Matcher<Parenthesis> {

  @Override
  public Parenthesis match(StringBuilder input) {

    if (input.indexIs('(') || input.indexIs(')')) {
      return Parenthesis.builder().type(input.take() == '(' ? Type.OPEN : Type.CLOSE).build();
    }

    return null;

  }

}
