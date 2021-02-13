package com.springfilter.node;

import java.util.Arrays;
import java.util.LinkedList;

import com.springfilter.FilterParser;
import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FilterMatcher extends Matcher<Filter> {

  public static final FilterMatcher INSTANCE = new FilterMatcher();

  @Override
  public Filter match(LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes)
      throws ParserException {
    return Filter.builder()
        .body((IPredicate) MatcherUtils.getNextExpression("The filter should be made of a predicate expression",
            (t -> !t.isEmpty()), new LinkedList<Matcher<?>>(Arrays.asList(FilterParser.matchers)), tokens, nodes))
        .build();
  }

}
