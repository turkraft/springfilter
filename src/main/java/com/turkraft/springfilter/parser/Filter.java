package com.turkraft.springfilter.parser;

import java.util.Objects;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import com.turkraft.springfilter.parser.FilterParser.FilterContext;
import com.turkraft.springfilter.parser.generator.query.QueryGenerator;

public class Filter extends ParserRuleContext {

  Filter() {}

  Filter(ParserRuleContext parent, int invokingStateNumber) {
    super(parent, invokingStateNumber);
  }

  public static FilterContext parse(String input) {
    Objects.requireNonNull(input);
    FilterLexer lexer = new FilterLexer(CharStreams.fromString(input));
    lexer.removeErrorListeners();
    lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
    FilterParser parser = new FilterParser(new CommonTokenStream(lexer));
    parser.removeErrorListeners();
    parser.addErrorListener(ThrowingErrorListener.INSTANCE);
    return parser.filter();
  }

  public static FilterContext from(String input) {
    return parse(input);
  }

  public String generate() {
    return QueryGenerator.run(this);
  }

  @Override
  public String toString() {
    return generate();
  }

}
