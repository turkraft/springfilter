package com.turkraft.springfilter.token;

import com.turkraft.springfilter.FilterExtensions;
import com.turkraft.springfilter.token.Parenthesis.Type;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
public class ParenthesisMatcher extends Matcher<Parenthesis> {

  @Override
  public Parenthesis match(StringBuilder input) {

    if (input.indexIs('(') || input.indexIs(')')) {
      return Parenthesis.builder().type(input.take() == '(' ? Type.OPEN : Type.CLOSE).build();
    }

    return null;

  }

}
