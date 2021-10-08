package com.turkraft.springfilter.parser.generator.query;

import java.util.Objects;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import com.turkraft.springfilter.exception.UnimplementFilterOperationException;
import com.turkraft.springfilter.parser.Filter;
import com.turkraft.springfilter.parser.FilterBaseVisitor;
import com.turkraft.springfilter.parser.FilterLexer;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.FilterParser.FilterContext;
import com.turkraft.springfilter.parser.FilterParser.PrefixContext;
import com.turkraft.springfilter.parser.operation.InfixOperation;
import com.turkraft.springfilter.parser.operation.PrefixOperation;

public class QueryGenerator extends FilterBaseVisitor<String> {

  public static String run(Filter filter) {
    Objects.requireNonNull(filter);
    String output = new QueryGenerator().visit(filter);
    new FilterParser(new CommonTokenStream(new FilterLexer(CharStreams.fromString(output))))
        .filter(); // this is done to validate the tree
    return output;
  }

  @Override
  public String visitFilter(FilterContext ctx) {
    if (ctx.predicate() == null) {
      return "";
    }
    return visit(ctx.predicate());
  }

  @Override
  public String visitPriority(FilterParser.PriorityContext ctx) {
    return "(" + visit(ctx.predicate()) + ")";
  }

  @Override
  public String visitPrefix(PrefixContext ctx) {

    PrefixOperation op = PrefixOperation.from(ctx.operator.getType());

    String left = visit(ctx.left);

    switch (op) {

      case IS_NULL:
        return left + " is null";

      case IS_EMPTY:
        return left + " is empty";

      case IS_NOT_NULL:
        return left + " is not null";

      case IS_NOT_EMPTY:
        return left + " is not empty";

    }

    throw new UnimplementFilterOperationException(op);

  }

  @Override
  public String visitInfix(FilterParser.InfixContext ctx) {
    InfixOperation op = InfixOperation.from(ctx.operator.getType());
    if (op == InfixOperation.IN) {
      return visitIn(ctx);
    }
    return visit(ctx.left) + " " + ctx.operator.getText() + " " + visit(ctx.right);
  }

  public String visitIn(FilterParser.InfixContext ctx) {
    return visit(ctx.left) + " in ("
        + ctx.arguments.stream().map(this::visit).collect(Collectors.joining(", ")) + ")";
  }

  @Override
  public String visitPostfix(FilterParser.PostfixContext ctx) {
    return ctx.operator.getText() + " " + visit(ctx.right);
  }

  @Override
  public String visitFunction(FilterParser.FunctionContext ctx) {
    return ctx.ID().getText() + "("
        + ctx.arguments.stream().map(this::visit).collect(Collectors.joining(", ")) + ")";
  }

  @Override
  public String visitField(FilterParser.FieldContext ctx) {
    return ctx.getText();
  }

  @Override
  public String visitInput(FilterParser.InputContext ctx) {
    return ctx.getText();
  }

  @Override
  protected String defaultResult() {
    return "?";
  }

  @Override
  protected String aggregateResult(String aggregate, String nextResult) {
    return aggregate + nextResult;
  }

}
