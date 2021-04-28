package com.turkraft.springfilter.compiler.node;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.compiler.token.input.IInput;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
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
