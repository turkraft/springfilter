package com.torshid.springfilter;

import java.util.LinkedList;

import com.torshid.compiler.Parser;
import com.torshid.compiler.exception.ParserException;
import com.torshid.compiler.token.Token;
import com.torshid.springfilter.node.Filter;
import com.torshid.springfilter.node.matcher.FilterMarcher;

public class FilterParser {

  private FilterParser() {}

  public static Filter parse(LinkedList<Token> tokens) throws ParserException {
    return Parser.parse(FilterMarcher.INSTANCE, tokens);
  }

}
