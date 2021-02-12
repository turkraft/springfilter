package com.torshid.compiler;

import java.util.LinkedList;

import com.torshid.compiler.exception.InvalidTokenSequenceException;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.INode;
import com.torshid.compiler.node.Matcher;
import com.torshid.compiler.node.IRoot;
import com.torshid.compiler.token.IToken;

public class Parser {

  private Parser() {}

  public static <N extends IRoot<N>> N parse(Matcher<N> matcher, LinkedList<IToken> tokens) throws ParserException {
    return matcher.match(tokens, new LinkedList<>());
  }

  public static INode walk(Matcher<?>[] matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes, boolean exception)
      throws ParserException {

    for (Matcher<?> matcher : matchers) {

      INode node = matcher.match(tokens, nodes);

      if (node != null) {
        return node;
      }

    }

    if (exception) {
      throw new InvalidTokenSequenceException(tokens);
    }

    return null;

  }

  public static INode walk(Matcher<?>[] matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes)
      throws ParserException {
    return walk(matchers, tokens, nodes, true);
  }

}
