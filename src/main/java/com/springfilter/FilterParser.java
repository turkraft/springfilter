package com.springfilter;

import java.util.Arrays;
import java.util.LinkedList;

import com.springfilter.compiler.Parser;
import com.springfilter.compiler.exception.ParserException;
import com.springfilter.compiler.node.INode;
import com.springfilter.compiler.node.Matcher;
import com.springfilter.compiler.token.IToken;
import com.springfilter.node.FieldMatcher;
import com.springfilter.node.Filter;
import com.springfilter.node.FilterMatcher;
import com.springfilter.node.FunctionMatcher;
import com.springfilter.node.InputMatcher;
import com.springfilter.node.predicate.ConditionMatcher;
import com.springfilter.node.predicate.OperationMatcher;
import com.springfilter.node.predicate.PriorityMatcher;

public class FilterParser {

  public static Matcher<?>[] matchers = new Matcher<?>[] {

      //      PriorityMatcher.INSTANCE, ConditionMatcher.INSTANCE, OperationMatcher.INSTANCE, InputMatcher.INSTANCE,
      //      FunctionMatcher.INSTANCE, FieldMatcher.INSTANCE

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

  public static <T extends INode> T walk(Class<T> klass, LinkedList<IToken> tokens, LinkedList<INode> nodes,
      boolean exception) throws ParserException {
    return Parser.walk(klass, new LinkedList<Matcher<?>>(Arrays.asList(matchers)), tokens, nodes, exception);
  }

}
