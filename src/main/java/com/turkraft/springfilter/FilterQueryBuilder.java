package com.turkraft.springfilter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.turkraft.springfilter.node.Arguments;
import com.turkraft.springfilter.node.Field;
import com.turkraft.springfilter.node.Filter;
import com.turkraft.springfilter.node.Function;
import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.node.Input;
import com.turkraft.springfilter.node.Nothing;
import com.turkraft.springfilter.node.predicate.ConditionInfix;
import com.turkraft.springfilter.node.predicate.ConditionPostfix;
import com.turkraft.springfilter.node.predicate.OperationInfix;
import com.turkraft.springfilter.node.predicate.OperationPrefix;
import com.turkraft.springfilter.node.predicate.Priority;
import com.turkraft.springfilter.token.Comparator;
import com.turkraft.springfilter.token.Operator;
import com.turkraft.springfilter.token.input.Bool;
import com.turkraft.springfilter.token.input.Numeral;
import com.turkraft.springfilter.token.input.Text;

public class FilterQueryBuilder {

  private FilterQueryBuilder() {}

  /* FILTER */

  public static Filter filter(IExpression body) {
    return Filter.builder().body(body).build();
  }

  /* NOTHING */

  public static Nothing nothing() {
    return Nothing.builder().build();
  }

  /* PRIORITY */

  public static Priority priority(IExpression body) {
    return Priority.builder().body(body).build();
  }

  /* FIELD */

  public static Field field(String name) {
    return Field.builder().name(name).build();
  }

  /* INPUTS */

  public static Input input(Number value) {
    return Input.builder().value(Numeral.builder().value(value).build()).build();
  }

  public static Input input(Boolean value) {
    return Input.builder().value(Bool.builder().value(value).build()).build();
  }

  public static Input input(Enum<?> value) {
    return Input.builder().value(Text.builder().value(value.toString()).build()).build();
  }

  public static Input input(String value) {
    return Input.builder().value(Text.builder().value(value).build()).build();
  }

  public static Input input(Date value) {
    // need to format
    return input((Object) value);
  }

  public static Input input(Object value) {
    return Input.builder().value(Text.builder().value(value != null ? value.toString() : null).build()).build();
  }

  /* INFIX CONDITIONS */

  public static ConditionInfix condition(IExpression left, Comparator comparator, IExpression right) {
    return ConditionInfix.builder().left(left).comparator(comparator).right(right).build();
  }

  /* EQUAL */

  public static ConditionInfix equal(IExpression left, IExpression right) {
    return condition(left, Comparator.EQUAL, right);
  }

  public static ConditionInfix equal(String field, IExpression value) {
    return equal(field(field), value);
  }

  public static IExpression equal(String field, String value) {
    return equal(field, string(value));
  }

  public static <T extends Number> IExpression equal(String field, T value) {
    return equal(field, number(value));
  }

  public static IExpression equal(String field, Boolean value) {
    return equal(field, bool(value));
  }

  public static <T extends Enum<T>> IExpression equal(String field, T value) {
    return equal(field, enum_(value));
  }

  /* NOT EQUAL */

  public static ConditionInfix notEqual(IExpression left, IExpression right) {
    return condition(left, Comparator.NOT_EQUAL, right);
  }

  public static ConditionInfix notEqual(String field, IExpression value) {
    return notEqual(field(field), value);
  }

  public static IExpression notEqual(String field, String value) {
    return notEqual(field, string(value));
  }

  public static <T extends Number> IExpression notEqual(String field, T value) {
    return notEqual(field, number(value));
  }

  public static IExpression notEqual(String field, Boolean value) {
    return notEqual(field, bool(value));
  }

  public static <T extends Enum<T>> IExpression notEqual(String field, T value) {
    return notEqual(field, enum_(value));
  }

  /* GREATER THAN */

  public static ConditionInfix greaterThan(IExpression left, IExpression right) {
    return condition(left, Comparator.GREATER_THAN, right);
  }

  public static ConditionInfix greaterThan(String field, IExpression value) {
    return greaterThan(field(field), value);
  }

  public static <T extends Number> IExpression greaterThan(String field, T value) {
    return greaterThan(field, number(value));
  }

  /* GREATER THAN OR EQUAL */

  public static ConditionInfix greaterThanOrEqual(IExpression left, IExpression right) {
    return condition(left, Comparator.GREATER_THAN_OR_EQUAL, right);
  }

  public static ConditionInfix greaterThanOrEqual(String field, IExpression value) {
    return greaterThanOrEqual(field(field), value);
  }

  public static <T extends Number> IExpression greaterThanOrEqual(String field, T value) {
    return greaterThanOrEqual(field, number(value));
  }

  /* LESS THAN */

  public static ConditionInfix lessThan(IExpression left, IExpression right) {
    return condition(left, Comparator.LESS_THAN, right);
  }

  public static ConditionInfix lessThan(String field, IExpression value) {
    return lessThan(field(field), value);
  }

  public static <T extends Number> IExpression lessThan(String field, T value) {
    return lessThan(field, number(value));
  }

  /* LESS THAN OR EQUAL */

  public static ConditionInfix lessThanOrEqual(IExpression left, IExpression right) {
    return condition(left, Comparator.LESS_THAN_OR_EQUAL, right);
  }

  public static ConditionInfix lessThanOrEqual(String field, IExpression value) {
    return lessThanOrEqual(field(field), value);
  }

  public static <T extends Number> IExpression lessThanOrEqual(String field, T value) {
    return lessThanOrEqual(field, number(value));
  }

  /* LIKE */

  public static ConditionInfix like(String field, Field pattern) {
    return condition(field(field), Comparator.LIKE, pattern);
  }

  public static ConditionInfix like(String field, String pattern) {
    return condition(field(field), Comparator.LIKE, input(pattern));
  }

  /* IN */

  public static ConditionInfix in(String field, List<IExpression> args) {
    return condition(field(field), Comparator.IN, Arguments.builder().values(args).build());
  }

  /* POSTFIX CONDITIONS */

  public static ConditionPostfix condition(IExpression left, Comparator comparator) {
    return ConditionPostfix.builder().left(left).comparator(comparator).build();
  }

  public static ConditionPostfix isNull(Field left) {
    return condition(left, Comparator.NULL);
  }

  public static ConditionPostfix isNull(String field) {
    return isNull(field(field));
  }

  public static ConditionPostfix isNotNull(Field left) {
    return condition(left, Comparator.NOT_NULL);
  }

  public static ConditionPostfix isNotNull(String field) {
    return isNotNull(field(field));
  }

  public static ConditionPostfix isEmpty(Field left) {
    return condition(left, Comparator.EMPTY);
  }

  public static ConditionPostfix isEmpty(String field) {
    return isEmpty(field(field));
  }

  public static ConditionPostfix isNotEmpty(Field left) {
    return condition(left, Comparator.NOT_EMPTY);
  }

  public static ConditionPostfix isNotEmpty(String field) {
    return isNotEmpty(field(field));
  }

  /* INFIX OPERATIONS */

  public static OperationInfix operation(IExpression left, Operator operator, IExpression right) {
    return OperationInfix.builder().left(left).operator(operator).right(right).build();
  }

  public static OperationInfix and(IExpression left, IExpression right) {
    return operation(left, Operator.AND, right);
  }

  public static IExpression and(IExpression... expressions) {
    if (expressions.length == 0)
      return null;
    if (expressions.length == 1)
      return expressions[1];
    IExpression ands = expressions[0];
    for (int i = 1; i < expressions.length; i++) {
      ands = and(ands, expressions[i]);
    }
    return ands;
  }

  public static OperationInfix or(IExpression left, IExpression right) {
    return operation(left, Operator.OR, right);
  }

  public static IExpression or(IExpression... expressions) {
    if (expressions.length == 0)
      return null;
    if (expressions.length == 1)
      return expressions[1];
    IExpression ors = expressions[0];
    for (int i = 1; i < expressions.length; i++) {
      ors = or(ors, expressions[i]);
    }
    return ors;
  }

  /* PREFIX OPERATIONS */

  public static OperationPrefix operation(Operator operator, IExpression right) {
    return OperationPrefix.builder().operator(operator).right(right).build();
  }

  public static OperationPrefix not(IExpression right) {
    return operation(Operator.NOT, right);
  }

  /* FUNCTIONS */

  public static Function function(String name, List<IExpression> args) {
    return Function.builder().name(name).arguments(Arguments.builder().values(args).build()).build();
  }

  public static Function function(Function.Type name, List<IExpression> args) {
    return function(name.name().toLowerCase(), args);
  }

  public static Function function(String name, IExpression... args) {
    return function(name, Arrays.asList(args));
  }

  public static Function function(Function.Type name, IExpression... args) {
    return function(name.name().toLowerCase(), args);
  }

  public static Function absolute(IExpression arg) {
    return function(Function.Type.ABSOLUTE, arg);
  }

  public static Function min(IExpression arg) {
    return function(Function.Type.MIN, arg);
  }

  public static Function max(IExpression arg) {
    return function(Function.Type.MAX, arg);
  }

  public static Function average(IExpression arg) {
    return function(Function.Type.AVERAGE, arg);
  }

  public static Function sum(IExpression arg) {
    return function(Function.Type.SUM, arg);
  }

  public static Function size(IExpression arg) {
    return function(Function.Type.SIZE, arg);
  }

  public static Function length(IExpression arg) {
    return function(Function.Type.LENGTH, arg);
  }

  public static Function trim(IExpression arg) {
    return function(Function.Type.TRIM, arg);
  }

  public static Function currentDate() {
    return function(Function.Type.CURRENTDATE);
  }

  public static Function currentTime() {
    return function(Function.Type.CURRENTTIME);
  }

  public static Function currentTimestamp() {
    return function(Function.Type.CURRENTTIMESTAMP);
  }

  /* HELPERS */

  public static IExpression object(Object arg) {
    if (arg == null)
      return nothing();
    return Input.builder().value(Text.builder().value(arg.toString()).build()).build();
  }

  public static <T extends Number> IExpression number(T arg) {
    return Input.builder().value(Numeral.builder().value(arg).build()).build();
  }

  public static IExpression bool(Boolean arg) {
    return Input.builder().value(Bool.builder().value(arg).build()).build();
  }

  public static IExpression string(String arg) {
    return object(arg);
  }

  public static <T extends Enum<T>> IExpression enum_(T arg) {
    return object(arg);
  }

  public static <T> List<IExpression> objects(List<T> args) {
    return args.stream().map(a -> object(a)).collect(Collectors.toList());
  }

  public static <T extends Number> List<IExpression> numbers(List<T> args) {
    return args.stream().map(a -> number(a)).collect(Collectors.toList());
  }

  public static List<IExpression> bools(List<Boolean> args) {
    return args.stream().map(a -> bool(a)).collect(Collectors.toList());
  }

  public static List<IExpression> strings(List<String> args) {
    return objects(args);
  }

  public static <T extends Enum<T>> List<IExpression> enums(List<T> args) {
    return objects(args);
  }

}
