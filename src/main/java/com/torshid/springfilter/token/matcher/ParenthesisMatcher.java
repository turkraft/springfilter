package com.torshid.springfilter.token.matcher;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.token.matcher.Matcher;
import com.torshid.springfilter.token.Parenthesis;
import com.torshid.springfilter.token.Parenthesis.Type;

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
