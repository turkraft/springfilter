package com.springfilter.node;

import java.util.LinkedList;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;
import com.springfilter.token.input.IInput;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class InputMatcher extends Matcher<IExpression> {

  public static final InputMatcher INSTANCE = new InputMatcher();

  @Override
  public IExpression match(LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes)
      throws ParserException {

    if (tokens.indexIs(IInput.class)) {
      return Input.builder().value((IInput) tokens.take()).build();
    }

    return null;

  }

}
