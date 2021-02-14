package com.turkraft.springfilter.compiler;

import java.util.LinkedList;
import java.util.function.Function;

import com.turkraft.springfilter.compiler.exception.ParserException;
import com.turkraft.springfilter.compiler.exception.TokenizerException;
import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.compiler.node.IRoot;
import com.turkraft.springfilter.compiler.token.IToken;

public class Compiler {

  private Compiler() {}

  public static LinkedList<IToken> tokenize(com.turkraft.springfilter.compiler.token.Matcher<?>[] tokenMatchers, String input)
      throws TokenizerException {
    return Tokenizer.tokenize(tokenMatchers, input);
  }

  public static <N extends IRoot<N>> N parse(com.turkraft.springfilter.compiler.node.Matcher<N> rootNodeMatcher,
      LinkedList<IToken> tokens) throws ParserException {
    return Parser.parse(rootNodeMatcher, tokens).transform(null);
  }

  public static <N extends IRoot<N>> String generate(N node) {
    return node.generate();
  }

  public static <N extends IRoot<N>, T> T generate(N node, Function<INode, T> func) {
    return node.generate(func);
  }

  public static <N extends IRoot<N>> String compile(com.turkraft.springfilter.compiler.token.Matcher<?>[] tokenMatchers,
      com.turkraft.springfilter.compiler.node.Matcher<N> rootNodeMatcher, String input) throws ParserException, TokenizerException {
    return generate(parse(rootNodeMatcher, tokenize(tokenMatchers, input)));
  }

  public static <N extends IRoot<N>, T> T compile(com.turkraft.springfilter.compiler.token.Matcher<?>[] tokenMatchers,
      com.turkraft.springfilter.compiler.node.Matcher<N> rootNodeMatcher, String input, Function<INode, T> func)
      throws ParserException, TokenizerException {
    return generate(parse(rootNodeMatcher, tokenize(tokenMatchers, input)), func);
  }

}
