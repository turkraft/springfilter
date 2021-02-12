package com.torshid.springfilter.test;

import java.util.LinkedList;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.FilterParser;
import com.torshid.springfilter.FilterTokenizer;
import com.torshid.springfilter.node.Filter;

class FilterParserTest {

  @ParameterizedTest
  @ValueSource(strings = {"a:1", "(b:2)", "(a : 1 or (b : 2 or c : 5))"})
  void test(String input) throws Exception {

    LinkedList<IToken> tokens = FilterTokenizer.tokenize(input);

    Filter ast = FilterParser.parse(tokens).transform(null);

    System.out.println(ast.generate());
  }

}
