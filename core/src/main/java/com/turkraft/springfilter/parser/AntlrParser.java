package com.turkraft.springfilter.parser;

import com.turkraft.springfilter.definition.FilterFunctions;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.definition.FilterPlaceholders;
import com.turkraft.springfilter.parser.AntlrFilterParser.CollectionContext;
import com.turkraft.springfilter.parser.AntlrFilterParser.ExpressionContext;
import com.turkraft.springfilter.parser.AntlrFilterParser.FieldContext;
import com.turkraft.springfilter.parser.AntlrFilterParser.FilterContext;
import com.turkraft.springfilter.parser.AntlrFilterParser.FunctionContext;
import com.turkraft.springfilter.parser.AntlrFilterParser.InputContext;
import com.turkraft.springfilter.parser.AntlrFilterParser.PlaceholderContext;
import com.turkraft.springfilter.parser.AntlrFilterParser.PrefixExpressionContext;
import com.turkraft.springfilter.parser.AntlrFilterParser.PriorityContext;
import com.turkraft.springfilter.parser.node.CollectionNode;
import com.turkraft.springfilter.parser.node.FieldNode;
import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.parser.node.InputNode;
import com.turkraft.springfilter.parser.node.PlaceholderNode;
import com.turkraft.springfilter.parser.node.PriorityNode;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.springframework.stereotype.Service;

@Service
class AntlrParser {

  private final FilterOperators operators;

  private final FilterPlaceholders placeholders;

  private final FilterFunctions functions;

  public AntlrParser(FilterOperators operators, FilterPlaceholders placeholders,
      FilterFunctions functions) {
    this.operators = operators;
    this.placeholders = placeholders;
    this.functions = functions;
  }

  public FilterNode parse(AntlrBaseContext ctx) {

    if (ctx instanceof FilterContext) {
      return parse(((FilterContext) ctx).expression());
    }

    if (ctx instanceof PriorityContext) {
      return new PriorityNode(parse(((PriorityContext) ctx).expression()));
    }

    if (ctx instanceof InputContext) {
      String text = ctx.getText().startsWith("'") && ctx.getText().endsWith("'")
          ? ctx.getText().substring(1, ctx.getText().length() - 1)
          : ctx.getText();
      return new InputNode(text.replace("\\'", "'"));
    }

    if (ctx instanceof FieldContext) {
      return new FieldNode(ctx.getText());
    }

    if (ctx instanceof PlaceholderContext) {
      return new PlaceholderNode(
          placeholders.getPlaceholder(
              ctx.getText().substring(1, ctx.getText().length() - 1)));
    }

    if (ctx instanceof CollectionContext) {
      return new CollectionNode(
          ((CollectionContext) ctx).items.stream().map(this::parse)
              .collect(Collectors.toList()));
    }

    if (ctx instanceof FunctionContext) {
      return new FunctionNode(
          functions.getFunction(((FunctionContext) ctx).ID().getText()),
          ((FunctionContext) ctx).arguments.stream().map(this::parse)
              .collect(Collectors.toList()));
    }

    if (ctx instanceof PrefixExpressionContext) {
      return operators.getPrefixOperator(ctx.getChild(0).getText())
          .toNode(parse((AntlrBaseContext) ctx.getChild(1)));
    }

    if (ctx instanceof ExpressionContext) {

      if (ctx.getChildCount() == 1) {
        return parse((AntlrBaseContext) ctx.getChild(0));
      } else if (ctx.getChildCount() == 2) {

        if (ctx.getChild(0) instanceof TerminalNode) {
          return operators.getPrefixOperator(ctx.getChild(0).getText())
              .toNode(parse((AntlrBaseContext) ctx.getChild(1)));
        }

        return operators.getPostfixOperator(ctx.getChild(1).getText())
            .toNode(parse((AntlrBaseContext) ctx.getChild(0)));

      } else {

        int lowestPriorityIndex = -1;
        int lowestPriorityValue = Integer.MAX_VALUE;

        for (int i = 0; i < ctx.getChildCount(); i++) {
          if (ctx.getChild(i) instanceof ExpressionContext) {
            if (((ExpressionContext) ctx.getChild(i))._p <= lowestPriorityValue) {
              lowestPriorityValue = ((ExpressionContext) ctx.getChild(i))._p;
              lowestPriorityIndex = i;
            }
          }
        }

        ExpressionContext subCtx = new ExpressionContext(ctx, 0);

        for (int i = 0; i < lowestPriorityIndex - 1; i++) {

          if (ctx.getChild(i) instanceof TerminalNode) {
            subCtx.addChild((TerminalNode) ctx.getChild(i));
          } else if (ctx.getChild(i) instanceof ParserRuleContext) {
            subCtx.addChild((ParserRuleContext) ctx.getChild(i));
          }

        }

        return operators.getInfixOperator(ctx.getChild(lowestPriorityIndex - 1).getText())
            .toNode(parse(subCtx),
                parse((AntlrBaseContext) ctx.getChild(lowestPriorityIndex)));

      }

    }

    throw new UnsupportedOperationException("Unsupported context " + ctx);

  }

}
