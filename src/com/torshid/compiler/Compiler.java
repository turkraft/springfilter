package com.torshid.compiler;

import java.util.LinkedList;
import java.util.function.Function;

import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.exception.TokenizerException;
import com.torshid.compiler.node.Node;
import com.torshid.compiler.node.Root;
import com.torshid.compiler.token.Token;

public class Compiler {

  private Compiler() {}

  public static LinkedList<Token> tokenize(com.torshid.compiler.token.matcher.Matcher<?>[] tokenMatchers, String input)
      throws TokenizerException {
    return Tokenizer.tokenize(tokenMatchers, input);
  }

  public static <N extends Root<N>> N parse(com.torshid.compiler.node.matcher.Matcher<N> rootNodeMatcher,
      LinkedList<Token> tokens) throws ParserException {
    return Parser.parse(rootNodeMatcher, tokens).transform(null);
  }

  public static <N extends Root<N>> String generate(N node) {
    return node.generate();
  }

  public static <N extends Root<N>, T> T generate(N node, Function<Node, T> func) {
    return node.generate(func);
  }

  public static <N extends Root<N>> String compile(com.torshid.compiler.token.matcher.Matcher<?>[] tokenMatchers,
      com.torshid.compiler.node.matcher.Matcher<N> rootNodeMatcher, String input)
      throws ParserException, TokenizerException {
    return generate(parse(rootNodeMatcher, tokenize(tokenMatchers, input)));
  }

  public static <N extends Root<N>, T> T compile(com.torshid.compiler.token.matcher.Matcher<?>[] tokenMatchers,
      com.torshid.compiler.node.matcher.Matcher<N> rootNodeMatcher, String input, Function<Node, T> func)
      throws ParserException, TokenizerException {
    return generate(parse(rootNodeMatcher, tokenize(tokenMatchers, input)), func);
  }

}
