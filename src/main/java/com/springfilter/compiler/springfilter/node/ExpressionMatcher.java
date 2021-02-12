package com.springfilter.compiler.springfilter.node;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.Parser;
import com.springfilter.compiler.compiler.exception.ParserException;
import com.springfilter.compiler.compiler.node.INode;
import com.springfilter.compiler.compiler.node.Matcher;
import com.springfilter.compiler.compiler.token.IToken;
import com.springfilter.compiler.springfilter.node.predicate.PriorityMatcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ExpressionMatcher extends Matcher<IExpression<?>> {

  public static final ExpressionMatcher INSTANCE = new ExpressionMatcher();

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      PriorityMatcher.INSTANCE, InputMatcher.INSTANCE, FieldMatcher.INSTANCE

  };

  @Override
  public IExpression<?> match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {
    return (IExpression<?>) Parser.walk(matchers, tokens, nodes, false);
  }

}
