package com.turkraft.springfilter.node;

import java.util.LinkedList;

import com.turkraft.springfilter.Extensions;
import com.turkraft.springfilter.FilterParser;
import com.turkraft.springfilter.exception.ExpressionExpectedException;
import com.turkraft.springfilter.token.IToken;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FilterMatcher extends Matcher<Filter> {

  public static final FilterMatcher INSTANCE = new FilterMatcher();

  @Override
  public Filter match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes) {

    IExpression body = FilterParser.run(tokens, nodes);

    if (body == null) {
      throw new ExpressionExpectedException("No expression detected when building the filter");
    }

    return Filter.builder().body(body).build();

  }

}
