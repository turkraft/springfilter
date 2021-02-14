package com.turkraft.springfilter;

import java.util.LinkedList;

import com.turkraft.springfilter.compiler.Parser;
import com.turkraft.springfilter.compiler.exception.ParserException;
import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.compiler.node.Matcher;
import com.turkraft.springfilter.compiler.token.IToken;
import com.turkraft.springfilter.node.FieldMatcher;
import com.turkraft.springfilter.node.Filter;
import com.turkraft.springfilter.node.FilterMatcher;
import com.turkraft.springfilter.node.FunctionMatcher;
import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.node.InputMatcher;
import com.turkraft.springfilter.node.predicate.ConditionMatcher;
import com.turkraft.springfilter.node.predicate.OperationMatcher;
import com.turkraft.springfilter.node.predicate.PriorityMatcher;

public class FilterParser {

  public static Matcher<?>[] matchers = new Matcher<?>[] {

      PriorityMatcher.INSTANCE, ConditionMatcher.INSTANCE, OperationMatcher.INSTANCE, FunctionMatcher.INSTANCE,
      FieldMatcher.INSTANCE, InputMatcher.INSTANCE,

  };

  private FilterParser() {}

  public static Filter parse(String input) {
    return parse(FilterTokenizer.tokenize(input));
  }

  public static Filter parse(LinkedList<IToken> tokens) throws ParserException {
    return Parser.parse(FilterMatcher.INSTANCE, tokens);
  }

  @SuppressWarnings("unchecked")
  public static <T extends IExpression> T walk(LinkedList<IToken> tokens, LinkedList<INode> nodes) {
    return (T) Parser.walk(matchers, tokens, nodes);
  }

  public static IExpression run(LinkedList<IToken> tokens, LinkedList<INode> nodes) {
    return (IExpression) Parser.run(matchers, tokens, nodes);
  }

}
