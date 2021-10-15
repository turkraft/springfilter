package com.turkraft.springfilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import com.turkraft.springfilter.exception.FilterBuilderException;
import com.turkraft.springfilter.parser.Filter;
import com.turkraft.springfilter.parser.FilterLexer;
import com.turkraft.springfilter.parser.FilterParser.FieldContext;
import com.turkraft.springfilter.parser.FilterParser.FilterContext;
import com.turkraft.springfilter.parser.FilterParser.FunctionContext;
import com.turkraft.springfilter.parser.FilterParser.InfixContext;
import com.turkraft.springfilter.parser.FilterParser.InputContext;
import com.turkraft.springfilter.parser.FilterParser.PostfixContext;
import com.turkraft.springfilter.parser.FilterParser.PredicateContext;
import com.turkraft.springfilter.parser.FilterParser.PrefixContext;
import com.turkraft.springfilter.parser.FilterParser.PriorityContext;
import com.turkraft.springfilter.parser.StringConverter;
import com.turkraft.springfilter.parser.operation.IOperation;
import com.turkraft.springfilter.parser.operation.InfixOperation;
import com.turkraft.springfilter.parser.operation.PostfixOperation;
import com.turkraft.springfilter.parser.operation.PrefixOperation;

public class FilterBuilder {

  private static Token token(int tokenType) {
    return new CommonToken(tokenType, StringConverter
        .cleanStringInput(FilterLexer.VOCABULARY.getDisplayName(tokenType)).toLowerCase());
  }

  private static Token token(IOperation operation) {
    return token(operation.getTokenType());
  }

  private static TerminalNode terminalNode(int tokenType, String text) {
    return new TerminalNodeImpl(new CommonToken(tokenType, text));
  }

  public static FilterContext filter(Filter predicate) {
    FilterContext ctx = new FilterContext(null, 0);
    ctx.addChild(predicate);
    return ctx;
  }

  public static Filter infix(
      Filter left,
      InfixOperation operation,
      Filter right,
      NullCascade nullCascade) {
    if (left == null || right == null)
      switch (nullCascade) {
        case TAKE_NONE_IF_ONE_NULL:
          return null;
        case TAKE_ONE_IF_ONE_NULL:
          if (left == null)
            return right;
          return left;
      }
    InfixContext ctx = new InfixContext(new PredicateContext());
    ctx.left = (PredicateContext) left;
    ctx.left.setParent(ctx);
    ctx.operator = token(operation);
    ctx.right = (PredicateContext) right;
    ctx.right.setParent(ctx);
    return ctx;
  }

  public static Filter prefix(Filter left, PrefixOperation operation) {
    PrefixContext ctx = new PrefixContext(new PredicateContext());
    ctx.operator = token(operation);
    ctx.left = (PredicateContext) left;
    ctx.left.setParent(ctx);
    return ctx;
  }

  public static Filter postfix(PostfixOperation operation, Filter right) {
    PostfixContext ctx = new PostfixContext(new PredicateContext());
    ctx.operator = token(operation);
    ctx.right = (PredicateContext) right;
    ctx.right.setParent(ctx);
    return ctx;
  }

  public static Filter priority(PredicateContext predicate) {
    PriorityContext ctx = new PriorityContext(new PredicateContext());
    ctx.addChild(predicate);
    return ctx;
  }

  public static Filter function(String name, Collection<Filter> arguments) {
    FunctionContext ctx = new FunctionContext(new PredicateContext());
    ctx.addChild(terminalNode(FilterLexer.ID, name));
    if (arguments != null)
      for (Filter argument : arguments)
        ctx.arguments.add((PredicateContext) argument);
    return ctx;
  }

  public static Filter function(String name, Filter... arguments) {
    return function(name, arguments != null ? Arrays.asList(arguments) : Collections.emptyList());
  }

  public static Filter field(String field) {
    FieldContext ctx = new FieldContext(new PredicateContext());
    ctx.addChild(terminalNode(FilterLexer.ID, field));
    return ctx;
  }

  public static Filter input(Object input) {
    if (input == null)
      return null;
    if (input instanceof PredicateContext)
      return (PredicateContext) input;
    if (!StringConverter.isSupportedAsInput(input))
      throw new FilterBuilderException("Object '" + input + "' is not supported as input");
    InputContext ctx = new InputContext(new PredicateContext());
    ctx.addChild(terminalNode(FilterLexer.STRING, "'" + StringConverter.convert(input) + "'"));
    return ctx;
  }

  public static <C extends Collection<?>> Collection<Filter> inputs(C inputs) {
    if (inputs == null)
      return null;
    return inputs.stream().map(FilterBuilder::input).collect(Collectors.toList());
  }

  public static Collection<Filter> inputs(Object... inputs) {
    if (inputs == null)
      return null;
    return inputs(Arrays.asList(inputs));
  }

  /* INFIX OPERATIONS */

  public static Filter and(Filter left, Filter right) {
    return infix(left, InfixOperation.AND, right, NullCascade.TAKE_ONE_IF_ONE_NULL);
  }

  public static Filter and(Collection<Filter> predicates) {
    return FilterUtils.requireNonNullElse(FilterUtils.merge(FilterBuilder::and, predicates), null);
  }

  public static Filter and(Filter... predicates) {
    if (predicates == null)
      return null;
    return and(Arrays.asList(predicates));
  }

  public static Filter or(Filter left, Filter right) {
    return infix(left, InfixOperation.OR, right, NullCascade.TAKE_ONE_IF_ONE_NULL);
  }

  public static Filter or(Collection<Filter> predicates) {
    return FilterUtils.requireNonNullElse(FilterUtils.merge(FilterBuilder::or, predicates), null);
  }

  public static Filter or(Filter... predicates) {
    if (predicates == null)
      return null;
    return or(Arrays.asList(predicates));
  }

  public static Filter in(Filter left, Collection<? extends Filter> arguments) {
    if (arguments == null || arguments.isEmpty())
      return null;
    InfixContext ctx = new InfixContext(new PredicateContext());
    // doing some trick here to obey the visitors
    ctx.left = (PredicateContext) left;
    ctx.operator = token(InfixOperation.IN);
    for (Filter argument : arguments) {
      if (argument == null)
        continue;
      ctx.arguments.add((PredicateContext) argument);
    }
    if (ctx.arguments.isEmpty())
      return null;
    return ctx;
  }

  public static Filter in(String field, Collection<? extends Filter> arguments) {
    return in(field(field), arguments);
  }

  public static Filter in(Filter left, Filter... predicates) {
    return in(left, Arrays.asList(predicates));
  }

  public static Filter in(String field, Filter... predicates) {
    return in(field(field), predicates);
  }

  public static Filter like(Filter left, Filter right) {
    return infix(left, InfixOperation.LIKE, right, NullCascade.TAKE_NONE_IF_ONE_NULL);
  }

  public static Filter like(String field, String pattern) {
    return like(field(field), input(pattern));
  }

  public static Filter equal(Filter left, Filter right) {
    return infix(left, InfixOperation.EQUAL, right, NullCascade.TAKE_NONE_IF_ONE_NULL);
  }

  public static Filter equal(String field, Object input) {
    return equal(field(field), input(input));
  }

  public static Filter notEqual(Filter left, Filter right) {
    return infix(left, InfixOperation.NOT_EQUAL, right, NullCascade.TAKE_NONE_IF_ONE_NULL);
  }

  public static Filter notEqual(String field, Object input) {
    return notEqual(field(field), input(input));
  }

  public static Filter greaterThan(Filter left, Filter right) {
    return infix(left, InfixOperation.GREATER_THAN, right, NullCascade.TAKE_NONE_IF_ONE_NULL);
  }

  public static Filter greaterThan(String field, Object input) {
    return greaterThan(field(field), input(input));
  }

  public static Filter greaterThanOrEqual(Filter left, Filter right) {
    return infix(left, InfixOperation.GREATER_THAN_OR_EQUAL, right,
        NullCascade.TAKE_NONE_IF_ONE_NULL);
  }

  public static Filter greaterThanOrEqual(String field, Object input) {
    return greaterThanOrEqual(field(field), input(input));
  }

  public static Filter lessThan(Filter left, Filter right) {
    return infix(left, InfixOperation.LESS_THAN, right, NullCascade.TAKE_NONE_IF_ONE_NULL);
  }

  public static Filter lessThan(String field, Object input) {
    return lessThan(field(field), input(input));
  }

  public static Filter lessThanOrEqual(Filter left, Filter right) {
    return infix(left, InfixOperation.LESS_THAN_OR_EQUAL, right, NullCascade.TAKE_NONE_IF_ONE_NULL);
  }

  public static Filter lessThanOrEqual(String field, Object input) {
    return lessThanOrEqual(field(field), input(input));
  }

  /* PREFIX OPERATIONS */

  public static Filter isNull(Filter predicate) {
    return prefix(predicate, PrefixOperation.IS_NULL);
  }

  public static Filter isNull(String field) {
    return isNull(field(field));
  }

  public static Filter isNotNull(Filter predicate) {
    return prefix(predicate, PrefixOperation.IS_NOT_NULL);
  }

  public static Filter isNotNull(String field) {
    return isNotNull(field(field));
  }

  public static Filter isEmpty(Filter predicate) {
    return prefix(predicate, PrefixOperation.IS_EMPTY);
  }

  public static Filter isEmpty(String field) {
    return isEmpty(field(field));
  }

  public static Filter isNotEmpty(Filter predicate) {
    return prefix(predicate, PrefixOperation.IS_NOT_EMPTY);
  }

  public static Filter isNotEmpty(String field) {
    return isNotEmpty(field(field));
  }

  /* POSTFIX OPERATIONS */

  public static Filter not(Filter predicate) {
    return postfix(PostfixOperation.NOT, predicate);
  }

  /* FUNCTIONS */

  public static Filter absolute(Filter argument) {
    return function("absolute", argument);
  }

  public static Filter absolute(String field) {
    return absolute(field(field));
  }

  public static Filter average(Filter argument) {
    return function("average", argument);
  }

  public static Filter average(String field) {
    return average(field(field));
  }

  public static Filter min(Filter argument) {
    return function("min", argument);
  }

  public static Filter min(String field) {
    return min(field(field));
  }

  public static Filter max(Filter argument) {
    return function("max", argument);
  }

  public static Filter max(String field) {
    return max(field(field));
  }

  public static Filter sum(Filter argument) {
    return function("sum", argument);
  }

  public static Filter sum(String field) {
    return sum(field(field));
  }

  public static Filter size(Filter argument) {
    return function("size", argument);
  }

  public static Filter size(String field) {
    return size(field(field));
  }

  public static Filter length(Filter argument) {
    return function("length", argument);
  }

  public static Filter length(String field) {
    return length(field(field));
  }

  public static Filter trim(Filter argument) {
    return function("trim", argument);
  }

  public static Filter trim(String field) {
    return trim(field(field));
  }

  public static Filter lower(Filter argument) {
    return function("lower", argument);
  }

  public static Filter lower(String field) {
    return lower(field(field));
  }

  public static Filter upper(Filter argument) {
    return function("upper", argument);
  }

  public static Filter upper(String field) {
    return upper(field(field));
  }

  public static Filter concat(Collection<Filter> arguments) {
    return function("concat", arguments);
  }

  public static Filter concat(Filter argument1, Filter argument2) {
    return concat(Arrays.asList(argument1, argument2));
  }

  public static Filter currentDate() {
    return function("currentDate");
  }

  public static Filter currentTime() {
    return function("currentTime");
  }

  public static Filter currentTimestamp() {
    return function("currentTimestamp");
  }

  public static Filter exists(Filter argument) {
    return function("exists", argument);
  }

  public enum NullCascade {
    TAKE_ONE_IF_ONE_NULL, TAKE_NONE_IF_ONE_NULL
  }

}
