package com.springfilter;

import java.util.LinkedList;

import com.springfilter.compiler.Parser;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.Filter;
import com.springfilter.node.FilterMatcher;

public class FilterParser {

  private FilterParser() {}

  public static Filter parse(String input) {
    return parse(FilterTokenizer.tokenize(input));
  }

  public static Filter parse(LinkedList<IToken> tokens) throws ParserException {
    return Parser.parse(FilterMatcher.INSTANCE, tokens);
  }

}
