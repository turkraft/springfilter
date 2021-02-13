package com.springfilter.compiler;

import java.util.Arrays;
import java.util.Iterator;
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
    return matcher.match(new LinkedList<>(Arrays.asList(matcher)), tokens, new LinkedList<>());
  }

  public static INode walk(LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes,
      boolean exception) throws ParserException {

    LinkedList<IToken> tokenBackup = tokens.copy();
    LinkedList<INode> nodeBackup = nodes.copy();
    LinkedList<Matcher<?>> matchersCopy = matchers.copy();

    Iterator<Matcher<?>> i = matchers.iterator();

    while (i.hasNext()) {

      Matcher<?> matcher = i.next();

      i.remove();

      INode node = matcher.match(matchers, tokens, nodes);

      if (node != null) {
        matchers.replaceWith(matchersCopy);
        return node;
      }

      tokens.replaceWith(tokenBackup);
      nodes.replaceWith(nodeBackup);

    }

    matchers.replaceWith(matchersCopy);

    if (exception) {
      throw new InvalidTokenSequenceException(tokens);
    }

    return null;

  }

  public static INode walk(LinkedList<Matcher<?>> matchers, LinkedList<IToken> tokens, LinkedList<INode> nodes)
      throws ParserException {
    return walk(matchers, tokens, nodes, true);
  }

}
