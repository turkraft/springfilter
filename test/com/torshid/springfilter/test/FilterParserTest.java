package com.torshid.springfilter.test;

import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import com.torshid.compiler.token.Token;
import com.torshid.springfilter.FilterParser;
import com.torshid.springfilter.FilterTokenizer;
import com.torshid.springfilter.node.Filter;

class FilterParserTest {

  @Test
  void test() throws Exception {

    LinkedList<Token> tokens = FilterTokenizer.tokenize(
        "not not (voiture.marque = 'fiat' OR voiture.marque = 'audi') AND voiture.km < 10000 OR x is null and (( not z is empty))");

    Filter ast = FilterParser.parse(tokens).transform(null);

    System.out.println(ast.generate());

  }

}
