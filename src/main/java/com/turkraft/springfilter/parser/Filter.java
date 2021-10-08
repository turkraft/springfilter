package com.turkraft.springfilter.parser;

import java.util.Objects;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import com.turkraft.springfilter.parser.FilterParser.FilterContext;
import com.turkraft.springfilter.parser.generator.query.QueryGenerator;

public class Filter extends ParserRuleContext {

  public static FilterContext parse(String input) {
    Objects.requireNonNull(input);
    return new FilterParser(new CommonTokenStream(new FilterLexer(CharStreams.fromString(input))))
        .filter();
  }

  public static FilterContext from(String input) {
    return parse(input);
  }

  Filter() {}

  Filter(ParserRuleContext parent, int invokingStateNumber) {
    super(parent, invokingStateNumber);
  }

  public String generate() {
    return QueryGenerator.run(this);
  }

  @Override
  public String toString() {
    return generate();
  }

}
