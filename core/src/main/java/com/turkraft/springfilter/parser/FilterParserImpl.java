package com.turkraft.springfilter.parser;

import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
class FilterParserImpl implements FilterParser {

  @Autowired
  private AntlrParser antlrParser;

  private final FilterOperators operators;

  public FilterParserImpl(FilterOperators operators) {
    this.operators = operators;
  }

  @Override
  public FilterNode parse(String input, @Nullable ParseContext ctx) {

    com.turkraft.springfilter.parser.AntlrFilterLexer lexer = new com.turkraft.springfilter.parser.AntlrFilterLexer(
        CharStreams.fromString(input),
        operators);
    lexer.removeErrorListeners();
    lexer.addErrorListener(ThrowingErrorListener.INSTANCE);

    com.turkraft.springfilter.parser.AntlrFilterParser parser = new com.turkraft.springfilter.parser.AntlrFilterParser(
        new CommonTokenStream(lexer),
        operators);
    parser.removeErrorListeners();
    parser.addErrorListener(ThrowingErrorListener.INSTANCE);

    return antlrParser.parse(parser.filter(), ctx);

  }

}
