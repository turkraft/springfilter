package com.turkraft.springfilter.parser;

import com.turkraft.springfilter.definition.FilterOperator;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.parser.node.FilterNode;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.beans.factory.annotation.Autowired;
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
  public FilterNode parse(String input) {

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

    return antlrParser.parse(parser.filter());

  }

  private List<FilterOperator> getSortedOperators(FilterOperators filterOperators) {

    List<FilterOperator> list = new LinkedList<>();

    list.addAll(filterOperators.getPrefixOperators());
    list.addAll(filterOperators.getInfixOperators());
    list.addAll(filterOperators.getPostfixOperators());

    list.sort(Comparator.comparingInt(FilterOperator::getPriority));

    return list;

  }

}
