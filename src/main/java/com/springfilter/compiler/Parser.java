package com.springfilter.compiler;

import java.util.LinkedList;

import com.springfilter.compiler.exception.InvalidTokenSequenceException;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.IRoot;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class Parser {

  private Parser() {}

  public static <N extends IRoot<N>> N parse(Matcher<N> matcher, LinkedList<IToken> tokens) throws ParserException {
    return matcher.match(tokens, new LinkedList<>());
  }

  @SuppressWarnings("unchecked")
  public static <T extends INode> T walk(Class<?> klass, LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens,
      LinkedList<INode> nodes, boolean exception) throws ParserException {

    LinkedList<IToken> tokenBackup = tokens.copy();
    LinkedList<INode> nodeBackup = nodes.copy();

    while (true) {

      int count = tokens.size();

      for (Matcher<?> matcher : matchers) {

        LinkedList<IToken> innerTokenBackup = tokens.copy();
        LinkedList<INode> innerNodeBackup = nodes.copy();

        INode node = matcher.match(tokens, nodes);

        if (node != null) {

          if (klass.isAssignableFrom(node.getClass())) {
            return (T) node;
          }

          else {
            nodes.add(node);
          }

        }

        else {
          tokens.replaceWith(innerTokenBackup);
          nodes.replaceWith(innerNodeBackup);
        }

      }

      if (tokens.size() == count) {
        break;
      }

    }

    if (exception) {
      throw new InvalidTokenSequenceException(tokens);
    }

    tokens.replaceWith(tokenBackup);
    nodes.replaceWith(nodeBackup);

    return null;

  }

  public static <T extends INode> T walk(Class<T> klass, LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens,
      LinkedList<INode> nodes) throws ParserException {
    return walk(klass, matchers, tokens, nodes, true);
  }

}
