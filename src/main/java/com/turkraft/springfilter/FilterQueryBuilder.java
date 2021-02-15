package com.turkraft.springfilter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.turkraft.springfilter.node.Field;
import com.turkraft.springfilter.node.Filter;
import com.turkraft.springfilter.node.Function;
import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.node.Input;
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

  public static ConditionInfix equal(IExpression left, IExpression right) {
    return condition(left, Comparator.EQUAL, right);
  }

  public static ConditionInfix notEqual(IExpression left, IExpression right) {
    return condition(left, Comparator.NOT_EQUAL, right);
  }

  public static ConditionInfix greaterThan(IExpression left, IExpression right) {
    return condition(left, Comparator.GREATER_THAN, right);
  }

  public static ConditionInfix greaterThanOrEqual(IExpression left, IExpression right) {
    return condition(left, Comparator.GREATER_THAN_OR_EQUAL, right);
  }

  public static ConditionInfix lessThan(IExpression left, IExpression right) {
    return condition(left, Comparator.LESS_THAN, right);
  }

  public static ConditionInfix lessThanOrEqual(IExpression left, IExpression right) {
    return condition(left, Comparator.LESS_THAN_OR_EQUAL, right);
  }

  public static ConditionInfix like(Field left, Field right) {
    return condition(left, Comparator.LIKE, right);
  }

  public static ConditionInfix like(Field left, String right) {
    return condition(left, Comparator.LIKE, input(right));
  }

  /* POSTFIX CONDITIONS */

  public static ConditionPostfix condition(IExpression left, Comparator comparator) {
    return ConditionPostfix.builder().left(left).comparator(comparator).build();
  }

  public static ConditionPostfix isNull(Field left) {
    return condition(left, Comparator.NULL);
  }

  public static ConditionPostfix isNotNull(Field left) {
    return condition(left, Comparator.NOT_NULL);
  }

  public static ConditionPostfix isEmpty(Field left) {
    return condition(left, Comparator.EMPTY);
  }

  public static ConditionPostfix isNotEmpty(Field left) {
    return condition(left, Comparator.NOT_EMPTY);
  }

  /* INFIX OPERATIONS */

  public static OperationInfix operation(IExpression left, Operator operator, IExpression right) {
    return OperationInfix.builder().left(left).operator(operator).right(right).build();
  }

  public static OperationInfix and(IExpression left, IExpression right) {
    return operation(left, Operator.AND, right);
  }

  public static OperationInfix or(IExpression left, IExpression right) {
    return operation(left, Operator.OR, right);
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
    return Function.builder().name(name).arguments(args).build();
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

  public static Function absolute(IExpression arg1) {
    return function(Function.Type.ABSOLUTE, arg1);
  }

  public static Function min(IExpression arg1) {
    return function(Function.Type.MIN, arg1);
  }

  public static Function max(IExpression arg1) {
    return function(Function.Type.MAX, arg1);
  }

  public static Function average(IExpression arg1) {
    return function(Function.Type.AVERAGE, arg1);
  }

  public static Function sum(IExpression arg1) {
    return function(Function.Type.SUM, arg1);
  }

  public static Function size(IExpression arg1) {
    return function(Function.Type.SIZE, arg1);
  }

  public static Function length(IExpression arg1) {
    return function(Function.Type.LENGTH, arg1);
  }

  public static Function trim(IExpression arg1) {
    return function(Function.Type.TRIM, arg1);
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

}
