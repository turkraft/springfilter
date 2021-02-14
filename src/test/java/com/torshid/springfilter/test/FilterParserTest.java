package com.torshid.springfilter.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.springfilter.FilterParser;
import com.springfilter.FilterTokenizer;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.Filter;

class FilterParserTest {

  @ParameterizedTest
  @ValueSource(strings = {"hello(x) : 1"})
  void test2(String input) {

    LinkedList<IToken> tokens = FilterTokenizer.tokenize(input);

    Filter ast = FilterParser.parse(tokens).transform(null);

    System.out.println(ast.generate());

  }

  @ParameterizedTest
  @ValueSource(strings = {"(((a : (((1:2))) or not not not(b : 2 or c : 5))))", "x:1 or y:2 and z:3 or mdr:4 and oo:5",
      "hello(x) : 1"})
  void test(String input) {

    LinkedList<IToken> tokens = FilterTokenizer.tokenize(input);

    Filter ast = FilterParser.parse(tokens).transform(null);

    System.out.println(ast.generate());

  }

  @ParameterizedTest
  @ValueSource(strings = {"x : 1", "(x : 1 or y : 2)", "not(not(x : 1))", "(x : 1 or (y : 2 and z : 3))"})
  void generationIsEqualToInput(String input) throws Exception {
    assertEquals(input, FilterParser.parse(FilterTokenizer.tokenize(input)).transform(null).generate());
  }

}
