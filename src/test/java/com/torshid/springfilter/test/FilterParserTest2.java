package com.torshid.springfilter.test;

import java.util.LinkedList;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.turkraft.springfilter.FilterParser;
import com.turkraft.springfilter.FilterTokenizer;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.node.Filter;

class FilterParserTest2 {

  @ParameterizedTest
  @ValueSource(strings = {"(1:2)"})
  void test2(String input) {

    LinkedList<IToken> tokens = FilterTokenizer.tokenize(input);

    Filter ast = FilterParser.parse(tokens).transform(null);

    System.out.println(ast.generate());

  }
  //  @ParameterizedTest
  //  @ValueSource(strings = {"(1:2) or (2:3 and 4:5 or 6:7 and not 8:9 or hello(776,(ooo:5)):5)"})
  //  void test2(String input) {
  //
  //    LinkedList<IToken> tokens = FilterTokenizer.tokenize(input);
  //
  //    Filter ast = FilterParser.parse(tokens).transform(null);
  //
  //    System.out.println(ast.generate());
  //
  //  }

}
