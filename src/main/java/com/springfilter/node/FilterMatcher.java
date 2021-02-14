package com.springfilter.node;

import java.util.LinkedList;

import com.springfilter.FilterParser;
import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.exception.ExpressionExpectedException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FilterMatcher extends Matcher<Filter> {

  public static final FilterMatcher INSTANCE = new FilterMatcher();

  @Override
  public Filter match(LinkedList<IToken> tokens, LinkedList<INode> nodes) {

    Filter search = Filter.builder().build();

    while (!tokens.isEmpty()) {
      nodes.add(FilterParser.walk(IExpression.class, tokens, nodes, true));
    }

    if (nodes.size() != 1 || !nodes.indexIs(IPredicate.class)) {
      throw new ExpressionExpectedException("Tokens should form an expression");
    }

    search.setBody((IPredicate) nodes.take());

    return search;

  }

}
