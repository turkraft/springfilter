package com.torshid.springfilter.test;

import java.util.LinkedList;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.springfilter.FilterParser;
import com.springfilter.FilterTokenizer;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.Filter;

class FilterParserTest2 {

  @ParameterizedTest
  @ValueSource(strings = {"hello(x) : 1"})
  void test2(String input) {

    LinkedList<IToken> tokens = FilterTokenizer.tokenize(input);

    Filter ast = FilterParser.parse(tokens).transform(null);

    System.out.println(ast.generate());

  }

}
