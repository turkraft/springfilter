package com.torshid.springfilter;

import java.util.LinkedList;

import com.torshid.compiler.Parser;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.node.Filter;
import com.torshid.springfilter.node.FilterMatcher;

public class FilterParser {

  private FilterParser() {}

  public static Filter parse(String input) {
    return parse(FilterTokenizer.tokenize(input));
  }

  public static Filter parse(LinkedList<IToken> tokens) throws ParserException {
    return Parser.parse(FilterMatcher.INSTANCE, tokens);
  }

}
