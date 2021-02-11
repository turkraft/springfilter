package com.torshid.springfilter.node.matcher;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.Parser;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.Node;
import com.torshid.compiler.node.matcher.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.Expression;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ExpressionMatcher extends Matcher<Expression> {

  public static final ExpressionMatcher INSTANCE = new ExpressionMatcher();

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      ConditionMatcher.INSTANCE, OperationMatcher.INSTANCE, PriorityMatcher.INSTANCE

  };

  @Override
  public Expression match(LinkedList<IToken> tokens, LinkedList<Node> nodes) throws ParserException {
    return (Expression) Parser.walk(matchers, tokens, nodes);
  }

}
