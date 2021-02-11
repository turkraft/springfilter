package com.torshid.springfilter.node.matcher;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.Parser;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.matcher.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.IExpression;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ExpressionMatcher extends Matcher<IExpression<?>> {

  public static final ExpressionMatcher INSTANCE = new ExpressionMatcher();

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      ConditionMatcher.INSTANCE, OperationMatcher.INSTANCE, PriorityMatcher.INSTANCE

  };

  @Override
  public IExpression<?> match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {
    return (IExpression<?>) Parser.walk(matchers, tokens, nodes);
  }

}
