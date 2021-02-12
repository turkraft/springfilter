package com.springfilter.compiler.springfilter.node;

import java.util.LinkedList;

import com.springfilter.compiler.compiler.Extensions;
import com.springfilter.compiler.compiler.exception.ExpressionExpectedException;
import com.springfilter.compiler.compiler.exception.ParserException;
import com.springfilter.compiler.compiler.node.INode;
import com.springfilter.compiler.compiler.node.Matcher;
import com.springfilter.compiler.compiler.token.IToken;
import com.springfilter.compiler.springfilter.node.predicate.PredicateMatcher;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FilterMatcher extends Matcher<Filter> {

  public static final FilterMatcher INSTANCE = new FilterMatcher();

  @Override
  public Filter match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    Filter search = Filter.builder().build();

    while (!tokens.isEmpty()) {
      nodes.add(PredicateMatcher.INSTANCE.match(tokens, nodes));
    }

    if (nodes.size() != 1 || nodes.index() == null) {
      throw new ExpressionExpectedException("Tokens should form an expression");
    }

    search.setBody((IPredicate) nodes.take());

    return search;

  }

}
