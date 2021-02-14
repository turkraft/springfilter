package com.turkraft.springfilter.compiler;

import java.util.LinkedList;

import com.turkraft.springfilter.compiler.exception.ParserException;
import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.compiler.node.IRoot;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.token.IToken;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(Extensions.class)
public class Parser {

  private Parser() {}

  public static <N extends IRoot<N>> N parse(Matcher<N> matcher, LinkedList<IToken> tokens) throws ParserException {
    return matcher.match(tokens, new LinkedList<>());
  }

  public static INode walk(Matcher<?>[] matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes) {

    LinkedList<IToken> tokenBackup = tokens.copy();
    LinkedList<INode> nodeBackup = nodes.copy();

    while (true) {

      int count = tokens.size();

      for (Matcher<?> matcher : matchers) {

        LinkedList<IToken> innerTokenBackup = tokens.copy();
        LinkedList<INode> innerNodeBackup = nodes.copy();

        INode node = matcher.match(tokens, nodes);

        if (node != null) {
          return node;
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

    tokens.replaceWith(tokenBackup);
    nodes.replaceWith(nodeBackup);

    return null;

  }

  public static INode run(Matcher<?>[] matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes) {

    while (tokens.size() > 0) {

      INode node = walk(matchers, tokens, nodes);

      if (node == null) {
        break;
      }

      nodes.add(node);

    }

    return nodes.pollLast();

  }

}
