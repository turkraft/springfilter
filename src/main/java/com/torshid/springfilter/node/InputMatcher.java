package com.torshid.springfilter.node;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.Parser;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.predicate.PriorityMatcher;
import com.torshid.springfilter.token.input.IInput;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class InputMatcher extends Matcher<IExpression<?>> {

  public static final InputMatcher INSTANCE = new InputMatcher();

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      PriorityMatcher.INSTANCE, FieldMatcher.INSTANCE

  };

  @Override
  public IExpression<?> match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    if (tokens.indexIs(IInput.class)) {
      return Input.builder().value((IInput) tokens.take()).build();
    }

    return (IExpression<?>) Parser.walk(matchers, tokens, nodes, false);

  }

}
