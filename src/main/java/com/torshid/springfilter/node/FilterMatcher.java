package com.torshid.springfilter.node;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.ExpressionExpectedException;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.predicate.PredicateMatcher;
import com.torshid.springfilter.token.predicate.IPredicate;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FilterMatcher extends Matcher<Filter> {

  public static final FilterMatcher INSTANCE = new FilterMatcher();

  @Override
  public Filter match(LinkedList<IToken> tokens, LinkedList<INode> nodes) throws ParserException {

    Filter search = Filter.builder().build();

    while (!tokens.isEmpty()) {

      nodes.add(PredicateMatcher.INSTANCE.match(tokens, nodes));

      if (nodes.size() != 1) {
        throw new ExpressionExpectedException("Tokens should form an expression");
      }

    }

    search.setBody((IPredicate) nodes.index());

    return search;

  }

}
