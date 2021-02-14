package com.springfilter.node;

import com.springfilter.compiler.Extensions;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class MatcherUtils {

  private MatcherUtils() {

  }

  //  public static IExpression getNextExpression(String message, Predicate<LinkedList<IToken>> predicate,
  //      LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens) {
  //    return getNextExpression(message, predicate, matchers, tokens, new LinkedList<>());
  //  }
  //
  //  public static IExpression getNextExpression(String message, Predicate<LinkedList<IToken>> predicate,
  //      LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes) {
  //
  //    while (predicate.test(tokens)) {
  //      INode node = FilterParser.walk(matchers, tokens, nodes, false);
  //      if (node == null) {
  //        break;
  //      }
  //      nodes.add(node);
  //    }
  //
  //    if (nodes.size() != 1 || nodes.index() == null || !IExpression.class.isAssignableFrom(nodes.index().getClass())) {
  //      throw new ExpressionExpectedException(message);
  //    }
  //
  //    return (IExpression) nodes.get(0);
  //
  //  }

}
