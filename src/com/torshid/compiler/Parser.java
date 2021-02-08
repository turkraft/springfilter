package com.torshid.compiler;

import java.util.LinkedList;

import com.torshid.compiler.exception.InvalidTokenSequenceException;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.node.Node;
import com.torshid.compiler.node.Root;
import com.torshid.compiler.node.matcher.Matcher;
import com.torshid.compiler.token.Token;

public class Parser {

  private Parser() {}

  public static <N extends Root<N>> N parse(Matcher<N> matcher, LinkedList<Token> tokens) throws ParserException {
    return matcher.match(tokens, new LinkedList<>());
  }

  public static Node walk(Matcher<?>[] matchers, LinkedList<Token> tokens, LinkedList<Node> nodes)
      throws ParserException {

    for (Matcher<?> matcher : matchers) {

      Node node = matcher.match(tokens, nodes);

      if (node != null) {
        return node;
      }

    }

    throw new InvalidTokenSequenceException(tokens);

  }

}
