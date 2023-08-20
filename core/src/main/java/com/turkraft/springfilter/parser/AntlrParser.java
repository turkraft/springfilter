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
import java.util.Objects;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.springframework.lang.Nullable;
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

  public FilterNode parse(AntlrBaseContext antlrCtx, @Nullable ParseContext ctx) {

    if (antlrCtx instanceof FilterContext) {
      return parse(((FilterContext) antlrCtx).expression(), ctx);
    }

    if (antlrCtx instanceof PriorityContext) {
      return map(ctx,
          new PriorityNode(parse(((PriorityContext) antlrCtx).expression(), ctx)));
    }

    if (antlrCtx instanceof InputContext) {
      String text = antlrCtx.getText().startsWith("'") && antlrCtx.getText().endsWith("'")
          ? antlrCtx.getText().substring(1, antlrCtx.getText().length() - 1)
          : antlrCtx.getText();
      return map(ctx, new InputNode(text.replace("\\'", "'")));
    }

    if (antlrCtx instanceof FieldContext) {
      return map(ctx, new FieldNode(
          ctx != null ? ctx.getFieldMapper().apply(antlrCtx.getText()) : antlrCtx.getText()));
    }

    if (antlrCtx instanceof PlaceholderContext) {
      return map(ctx, new PlaceholderNode(
          placeholders.getPlaceholder(
              antlrCtx.getText().substring(1, antlrCtx.getText().length() - 1))));
    }

    if (antlrCtx instanceof CollectionContext) {
      return map(ctx, new CollectionNode(
          ((CollectionContext) antlrCtx).items.stream()
              .map(antlrCtx1 -> parse(antlrCtx1, ctx))
              .collect(Collectors.toList())));
    }

    if (antlrCtx instanceof FunctionContext) {
      return map(ctx, new FunctionNode(
          functions.getFunction(((FunctionContext) antlrCtx).ID().getText()),
          ((FunctionContext) antlrCtx).arguments.stream()
              .map(antlrCtx1 -> parse(antlrCtx1, ctx))
              .collect(Collectors.toList())));
    }

    if (antlrCtx instanceof PrefixExpressionContext) {
      return map(ctx, operators.getPrefixOperator(antlrCtx.getChild(0).getText())
          .toNode(parse((AntlrBaseContext) antlrCtx.getChild(1), ctx)));
    }

    if (antlrCtx instanceof ExpressionContext) {

      if (antlrCtx.getChildCount() == 1) {
        return map(ctx, parse((AntlrBaseContext) antlrCtx.getChild(0), ctx));
      } else if (antlrCtx.getChildCount() == 2) {

        if (antlrCtx.getChild(0) instanceof TerminalNode) {
          return map(ctx, operators.getPrefixOperator(antlrCtx.getChild(0).getText())
              .toNode(parse((AntlrBaseContext) antlrCtx.getChild(1), ctx)));
        }

        return map(ctx, operators.getPostfixOperator(antlrCtx.getChild(1).getText())
            .toNode(parse((AntlrBaseContext) antlrCtx.getChild(0), ctx)));

      } else {

        int lowestPriorityIndex = -1;
        int lowestPriorityValue = Integer.MAX_VALUE;

        for (int i = 0; i < antlrCtx.getChildCount(); i++) {
          if (antlrCtx.getChild(i) instanceof ExpressionContext) {
            if (((ExpressionContext) antlrCtx.getChild(i))._p <= lowestPriorityValue) {
              lowestPriorityValue = ((ExpressionContext) antlrCtx.getChild(i))._p;
              lowestPriorityIndex = i;
            }
          }
        }

        ExpressionContext subCtx = new ExpressionContext(antlrCtx, 0);

        for (int i = 0; i < lowestPriorityIndex - 1; i++) {

          if (antlrCtx.getChild(i) instanceof TerminalNode) {
            subCtx.addChild((TerminalNode) antlrCtx.getChild(i));
          } else if (antlrCtx.getChild(i) instanceof ParserRuleContext) {
            subCtx.addChild((ParserRuleContext) antlrCtx.getChild(i));
          }

        }

        return map(ctx,
            operators.getInfixOperator(antlrCtx.getChild(lowestPriorityIndex - 1).getText())
                .toNode(parse(subCtx, ctx),
                    parse((AntlrBaseContext) antlrCtx.getChild(lowestPriorityIndex), ctx)));

      }

    }

    throw new UnsupportedOperationException("Unsupported context " + antlrCtx);

  }

  private FilterNode map(@Nullable ParseContext ctx, FilterNode input) {
    if (ctx == null) {
      return input;
    }
    return Objects.requireNonNullElse(ctx.getNodeMapper().apply(input), input);
  }

}
