package com.turkraft.springfilter.node;

import java.util.LinkedList;
import com.turkraft.springfilter.FilterExtensions;
import com.turkraft.springfilter.token.IToken;
import com.turkraft.springfilter.token.input.IInput;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(FilterExtensions.class)
public class InputMatcher extends Matcher<IExpression> {

  public static final InputMatcher INSTANCE = new InputMatcher();

  @Override
  public IExpression match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes) {

    if (tokens.indexIs(IInput.class)) {
      return Input.builder().value((IInput) tokens.take()).build();
    }

    return null;

  }

}
