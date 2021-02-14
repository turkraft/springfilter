package com.springfilter.node;

import java.util.LinkedList;

import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class FilterMatcher extends Matcher<Filter> {

  public static final FilterMatcher INSTANCE = new FilterMatcher();

  @Override
  public Filter match(LinkedList<IToken> tokens, LinkedList<INode> nodes) {
    return Filter.builder()
        .body((IPredicate) MatcherUtils.run(tokens, nodes, "The filter should be made of an expression")).build();
  }

}
