package com.turkraft.springfilter.compiler.node;

import java.util.LinkedList;
import com.turkraft.springfilter.compiler.Parser;
import com.turkraft.springfilter.compiler.Extensions;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.exception.ExpressionExpectedException;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FilterMatcher extends Matcher<Filter> {

  public static final FilterMatcher INSTANCE = new FilterMatcher();

  @Override
  public Filter match(LinkedList<IToken> tokens, LinkedList<IExpression> nodes) {

    IExpression body = Parser.run(tokens, nodes);

    if (body == null) {
      throw new ExpressionExpectedException("No expression detected when building the filter");
    }

    return Filter.builder().body(body).build();

  }

}
