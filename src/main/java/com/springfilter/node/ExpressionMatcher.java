package com.springfilter.node;

import java.util.LinkedList;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.Parser;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.predicate.PriorityMatcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class ExpressionMatcher extends Matcher<IExpression> {

  public static final ExpressionMatcher INSTANCE = new ExpressionMatcher();

  private static Matcher<?>[] matchers = new Matcher<?>[] {

      InputMatcher.INSTANCE, FieldMatcher.INSTANCE, FunctionMatcher.INSTANCE, PriorityMatcher.INSTANCE

  };

  @Override
  public IExpression match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {
    return (IExpression) Parser.walk(matchers, tokens, nodes, true);
  }

}
