package com.torshid.springfilter.node.matcher;

import java.util.LinkedList;

import com.torshid.compiler.Extensions;
import com.torshid.compiler.exception.ExpressionExpectedException;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.Node;
import com.torshid.compiler.node.matcher.Matcher;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.Filter;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FilterMarcher extends Matcher<Filter> {

  public static final FilterMarcher INSTANCE = new FilterMarcher();

  @Override
  public Filter match(LinkedList<IToken> tokens, LinkedList<Node> nodes) throws ParserException {

    Filter search = Filter.builder().build();

    while (!tokens.isEmpty()) {

      // merging walked expressions to the ast body

      LinkedList<Node> subNodes = new LinkedList<Node>();

      if (search.getBody() != null) {
        subNodes.add(search.getBody());
      }

      try {

        search.setBody(ExpressionMatcher.INSTANCE.match(tokens, subNodes));

      } catch (ParserException ex) {
        throw new ExpressionExpectedException("Tokens should form an expression", ex);
      }

    }

    return search;

  }

}
