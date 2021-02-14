package com.springfilter.node;

import java.util.LinkedList;

import com.springfilter.FilterParser;
import com.springfilter.compiler.Extensions;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.token.IToken;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class MatcherUtils {

  private MatcherUtils() {

  }

  public static IExpression run(LinkedList<IToken> tokens, LinkedList<INode> nodes) {
    return run(tokens, nodes, null);
  }

  public static IExpression run(LinkedList<IToken> tokens, LinkedList<INode> nodes, String exceptionMessage) {

    while (tokens.size() > 0) {

      INode node = FilterParser.walk(IExpression.class, tokens, nodes, false);

      if (node == null) {
        break;
      }

      nodes.add(node);

    }

    return (IExpression) nodes.pollLast();

  }

}
