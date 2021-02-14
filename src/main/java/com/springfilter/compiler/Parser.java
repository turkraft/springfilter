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

  public static <T extends INode> T walk(Class<T> klass, LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens,
      LinkedList<INode> nodes) throws ParserException {
    return walk(klass, matchers, tokens, nodes, true);
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

          return (T) node;

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

  //  public static INode run(String message, LinkedList<IToken> tokens, LinkedList<INode> nodes) {
  //
  //    while (tokens.size() > 0) {
  //
  //      INode node = FilterParser.walk(IExpression.class, tokens, nodes, false);
  //      if (node == null) {
  //        break;
  //      }
  //      nodes.add(node);
  //
  //    }
  //
  //    if (nodes.size() != 1 || nodes.index() == null) {
  //      throw new ExpressionExpectedException(message);
  //    }
  //
  //    return nodes.get(0);
  //
  //  }

}
