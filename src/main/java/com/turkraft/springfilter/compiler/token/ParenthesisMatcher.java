package com.turkraft.springfilter.compiler.token;

import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.Parenthesis.Type;
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
