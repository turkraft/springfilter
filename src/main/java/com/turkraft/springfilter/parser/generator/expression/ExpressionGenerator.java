package com.turkraft.springfilter.parser.generator.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.antlr.v4.runtime.tree.ParseTreeProperty;

import com.turkraft.springfilter.FilterFunction;
import com.turkraft.springfilter.FilterParameters;
import com.turkraft.springfilter.FilterUtils;
import com.turkraft.springfilter.exception.BadFilterFunctionUsageException;
import com.turkraft.springfilter.exception.BadFilterSyntaxException;
import com.turkraft.springfilter.exception.InternalFilterException;
import com.turkraft.springfilter.exception.UnimplementFilterOperationException;
import com.turkraft.springfilter.exception.UnknownFilterFunctionException;
import com.turkraft.springfilter.parser.Filter;
import com.turkraft.springfilter.parser.FilterBaseVisitor;
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
import com.turkraft.springfilter.parser.operation.InfixOperation;
import com.turkraft.springfilter.parser.operation.PostfixOperation;
import com.turkraft.springfilter.parser.operation.PrefixOperation;

public class ExpressionGenerator extends FilterBaseVisitor<Expression<?>> {

  public static Expression<?> run(Filter filter, Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<?, ?>> joins, Object payload) {
    Objects.requireNonNull(filter);
    Objects.requireNonNull(root);
    Objects.requireNonNull(criteriaQuery);
    Objects.requireNonNull(criteriaBuilder);
    Objects.requireNonNull(joins);
    return new ExpressionGenerator(root, criteriaQuery, criteriaBuilder, joins, payload).visit(filter);
  }

  public static Expression<?> run(Filter filter, Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<?, ?>> joins) {
    return run(filter, root, criteriaQuery, criteriaBuilder, joins, null);
  }

  public static Expression<?> run(Filter filter, Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    return run(filter, root, criteriaQuery, criteriaBuilder, new HashMap<String, Join<?, ?>>());
  }

  public static Expression<?> run(String query, Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<?, ?>> joins, Object payload) {
    Objects.requireNonNull(query);
    Objects.requireNonNull(root);
    Objects.requireNonNull(criteriaQuery);
    Objects.requireNonNull(criteriaBuilder);
    Objects.requireNonNull(joins);
    return new ExpressionGenerator(root, criteriaQuery, criteriaBuilder, joins, payload).visit(Filter.from(query));
  }

  public static Expression<?> run(String query, Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<?, ?>> joins) {
    return run(query, root, criteriaQuery, criteriaBuilder, joins, null);
  }

  public static Expression<?> run(String query, Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    return run(query, root, criteriaQuery, criteriaBuilder, new HashMap<String, Join<?, ?>>());
  }

  private final Root<?> root;
  private final CriteriaQuery<?> criteriaQuery;
  private final CriteriaBuilder criteriaBuilder;
  private final Map<String, Join<?, ?>> joins;
  private Object payload;

  private final ParseTreeProperty<Class<?>> expectedInputTypes;

  protected ExpressionGenerator(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins, Object payload) {
    this.root = root;
    this.criteriaQuery = criteriaQuery;
    this.criteriaBuilder = criteriaBuilder;
    this.joins = joins;
    this.payload = payload;
    this.expectedInputTypes = new ParseTreeProperty<Class<?>>();
  }

  @Override
  public Expression<?> visitFilter(FilterContext ctx) {
    if (ctx.predicate() == null) {
      return null;
    }
    return visit(ctx.predicate());
  }

  @Override
  public Expression<?> visitPriority(PriorityContext ctx) {
    return visit(ctx.predicate());
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> visitPrefix(PrefixContext ctx) {

    PrefixOperation op = PrefixOperation.from(ctx.operator.getType());

    Expression<?> left = visit(ctx.left);

    switch (op) {

      case IS_NULL:
      case IS_EMPTY:

        if (Collection.class.isAssignableFrom(getJavaType(left))) {
          return criteriaBuilder.isEmpty((Expression<Collection<?>>) left);
        } else {
          return criteriaBuilder.isNull(left);
        }

      case IS_NOT_NULL:
      case IS_NOT_EMPTY:

        if (Collection.class.isAssignableFrom(getJavaType(left))) {
          return criteriaBuilder.isNotEmpty((Expression<Collection<?>>) left);
        } else {
          return criteriaBuilder.isNotNull(left);
        }

    }

    throw new UnimplementFilterOperationException(op);

  }

  @SuppressWarnings({"unchecked"})
  @Override
  public Expression<?> visitInfix(InfixContext ctx) {

    InfixOperation op = InfixOperation.from(ctx.operator.getType());

    switch (op) {

      case AND:
        return criteriaBuilder.and((Expression<Boolean>) visit(ctx.left), (Expression<Boolean>) visit(ctx.right));

      case OR:
        return criteriaBuilder.or((Expression<Boolean>) visit(ctx.left), (Expression<Boolean>) visit(ctx.right));

      case LIKE:
      case EQUAL:
      case NOT_EQUAL:
      case GREATER_THAN:
      case GREATER_THAN_OR_EQUAL:
      case LESS_THAN:
      case LESS_THAN_OR_EQUAL:
        return visitComparison(ctx, op);

      case IN:
        return visitIn(ctx);

    }

    throw new UnimplementFilterOperationException(op);

  }

  @SuppressWarnings({"unchecked", "rawtypes", "incomplete-switch"})
  private Expression<?> visitComparison(InfixContext ctx, InfixOperation op) {

    if (op == InfixOperation.LIKE) {
      return visitLike(ctx);
    }

    Expression<?> left = null;
    Expression<?> right = null;

    if (ctx.left instanceof InputContext && ctx.right instanceof InputContext) {
      throw new BadFilterSyntaxException("Both sides of the operation " + op + " can't be inputs");
    }

    else if (ctx.right instanceof InputContext) {
      left = visit(ctx.left);
      expectedInputTypes.put(ctx.right, getJavaType(left));
      right = visit(ctx.right);
    }

    else if (ctx.left instanceof InputContext) {
      right = visit(ctx.right);
      expectedInputTypes.put(ctx.left, getJavaType(right));
      left = visit(ctx.left);
    }

    else {
      left = visit(ctx.left);
      right = visit(ctx.right);
    }

    switch (op) {

      case EQUAL:
        return criteriaBuilder.equal(left, right);

      case NOT_EQUAL:
        return criteriaBuilder.notEqual(left, right);

      case GREATER_THAN:
        return criteriaBuilder.greaterThan((Expression<? extends Comparable>) left,
            (Expression<? extends Comparable>) right);

      case GREATER_THAN_OR_EQUAL:
        return criteriaBuilder.greaterThanOrEqualTo((Expression<? extends Comparable>) left,
            (Expression<? extends Comparable>) right);

      case LESS_THAN:
        return criteriaBuilder.lessThan((Expression<? extends Comparable>) left,
            (Expression<? extends Comparable>) right);

      case LESS_THAN_OR_EQUAL:
        return criteriaBuilder.lessThanOrEqualTo((Expression<? extends Comparable>) left,
            (Expression<? extends Comparable>) right);

    }

    throw new UnimplementFilterOperationException(op);

  }

  @SuppressWarnings({"unchecked"})
  private Expression<?> visitLike(InfixContext ctx) {

    expectedInputTypes.put(ctx.left, String.class);
    expectedInputTypes.put(ctx.right, String.class);

    Expression<?> left = visit(ctx.left), right = visit(ctx.right);

    if (ctx.right instanceof InputContext) {
      String pattern = StringConverter.cleanStringInput(ctx.right.getText());
      if (ExpressionGeneratorParameters.ENABLE_ASTERISK_WITH_LIKE_OPERATOR) {
        pattern = pattern.replace('*', '%');
      }
      right = criteriaBuilder.literal(pattern);
    }

    if (Number.class.isAssignableFrom(left.getJavaType())) {
      left = left.as(String.class); // cast number to string
    }

    if (!FilterParameters.CASE_SENSITIVE_LIKE_OPERATOR) {
      left = criteriaBuilder.upper((Expression<String>) left);
      right = criteriaBuilder.upper((Expression<String>) right);
    }

    return criteriaBuilder.like((Expression<String>) left, (Expression<String>) right);

  }

  private Expression<?> visitIn(InfixContext ctx) {

    if (ctx.left instanceof InputContext) {
      throw new BadFilterSyntaxException("Left-hand side of the IN operation can't be an input");
    }

    Expression<?> left = visit(ctx.left);

    In<Object> in = criteriaBuilder.in(left);

    for (PredicateContext argument : ctx.arguments) {

      Expression<?> expression = null;

      if (argument instanceof InputContext) {
        expectedInputTypes.put(argument, getJavaType(left));
        expression = visit(argument);
      } else {
        expression = visit(argument);
      }

      in.value(expression);

    }

    return in;

  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> visitPostfix(PostfixContext ctx) {

    PostfixOperation op = PostfixOperation.from(ctx.operator.getType());

    switch (op) {

      case NOT:
        return criteriaBuilder.not((Expression<Boolean>) visit(ctx.right));

    }

    throw new UnimplementFilterOperationException(op);

  }

  @Override
  public Expression<?> visitFunction(FunctionContext ctx) {

    String functionName = ctx.ID().getText().toLowerCase();

    switch (functionName) {

      case "abs":
      case "absolute":
        return criteriaBuilder.abs(getFunctionArgument(ctx, 0, Number.class));

      case "avg":
      case "average":
        return criteriaBuilder.avg(getFunctionArgument(ctx, 0, Number.class));

      case "min":
        return criteriaBuilder.min(getFunctionArgument(ctx, 0, Number.class));

      case "max":
        return criteriaBuilder.max(getFunctionArgument(ctx, 0, Number.class));

      case "sum":
        if (ctx.arguments.size() == 1) {
          return criteriaBuilder.sum(getFunctionArgument(ctx, 0, Number.class));
        }
        List<Expression<Number>> sumArguments = new ArrayList<Expression<Number>>();
        for (int i = 0; i < ctx.arguments.size(); i++) {
          sumArguments.add(getFunctionArgument(ctx, i, Number.class));
        }
        return FilterUtils.merge(criteriaBuilder::sum, sumArguments);

      case "diff":
        return criteriaBuilder.diff(getFunctionArgument(ctx, 0, Number.class),
            getFunctionArgument(ctx, 1, Number.class));

      case "prod":
        return criteriaBuilder.prod(getFunctionArgument(ctx, 0, Number.class),
            getFunctionArgument(ctx, 1, Number.class));

      case "quot":
        return criteriaBuilder.quot(getFunctionArgument(ctx, 0, Number.class),
            getFunctionArgument(ctx, 1, Number.class));

      case "mod":
        return criteriaBuilder.mod(getFunctionArgument(ctx, 0, Integer.class),
            getFunctionArgument(ctx, 1, Integer.class));

      case "sqrt":
        return criteriaBuilder.sqrt(getFunctionArgument(ctx, 0, Number.class));

      case "size":
        return criteriaBuilder.size(getFunctionArgument(ctx, 0, Collection.class));

      case "length":
        return criteriaBuilder.length(getFunctionArgument(ctx, 0, String.class));

      case "trim":
        return criteriaBuilder.trim(getFunctionArgument(ctx, 0, String.class));

      case "upper":
        return criteriaBuilder.upper(getFunctionArgument(ctx, 0, String.class));

      case "lower":
        return criteriaBuilder.lower(getFunctionArgument(ctx, 0, String.class));

      case "concat":
        List<Expression<String>> concatArguments = new ArrayList<Expression<String>>();
        for (int i = 0; i < ctx.arguments.size(); i++) {
          concatArguments.add(getFunctionArgument(ctx, i, String.class));
        }
        return FilterUtils.merge(criteriaBuilder::concat, concatArguments);

      case "currenttime":
        return criteriaBuilder.currentTime();

      case "currentdate":
        return criteriaBuilder.currentDate();

      case "currenttimestamp":
        return criteriaBuilder.currentTimestamp();

      case "exists":
        return criteriaBuilder.exists(getSubquery(ctx));

    }

    if (FilterParameters.CUSTOM_FUNCTIONS != null) {
      for (FilterFunction filterFunction : FilterParameters.CUSTOM_FUNCTIONS) {
        if (filterFunction.getName().equalsIgnoreCase(functionName)) {
          Expression<?>[] args =
              new Expression[filterFunction.getInputTypes() != null ? filterFunction.getInputTypes().length : 0];
          for (int i = 0; i < args.length; i++) {
            args[i] = getFunctionArgument(ctx, i, filterFunction.getInputTypes()[i]);
          }
          return criteriaBuilder.function(functionName, filterFunction.getOutputType(), args);
        }
      }
    }

    throw new UnknownFilterFunctionException("Unknown function '" + ctx.ID().getText() + "'");

  }

  @SuppressWarnings("unchecked")
  private <T> Expression<T> getFunctionArgument(FunctionContext ctx, int index, Class<T> expectedClass) {

    if (index >= ctx.arguments.size()) {
      throw new BadFilterFunctionUsageException(
          "The function '" + ctx.ID().getText() + "' expects at least " + (index + 1) + " arguments");
    }

    if (ctx.arguments.get(index) instanceof InputContext) {
      expectedInputTypes.put(ctx.arguments.get(index), expectedClass);
    }

    return (Expression<T>) visit(ctx.arguments.get(index));

  }

  @SuppressWarnings({"unchecked"})
  private Subquery<Integer> getSubquery(FunctionContext ctx) {

    if (ctx.arguments.size() != 1) {
      throw new BadFilterFunctionUsageException("The function '" + ctx.ID().getText() + "' needs one argument");
    }

    Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);

    Root<?> subroot = subquery.correlate(root);

    Expression<?> predicate = ExpressionGenerator.run(ctx.arguments.get(0), subroot, criteriaQuery, criteriaBuilder,
        new HashMap<String, Join<?, ?>>());

    if (!Boolean.class.isAssignableFrom(predicate.getJavaType())) {
      throw new BadFilterFunctionUsageException(
          "The function '" + ctx.ID().getText() + "' needs a predicate as its argument");
    }

    subquery.select(criteriaBuilder.literal(1));
    subquery.where((Expression<Boolean>) predicate);

    return subquery;

  }

  @Override
  public Expression<?> visitField(FieldContext ctx) {
    return ExpressionGeneratorUtils.getDatabasePath(root, joins, payload, ctx.getText(),
        ExpressionGeneratorParameters.FILTERING_AUTHORIZATION);
  }

  @Override
  public Expression<?> visitInput(InputContext ctx) {

    if (expectedInputTypes.get(ctx) == null) {
      throw new InternalFilterException("The expected class should be set previous to visiting the input");
    }

    Class<?> expectedInputType = expectedInputTypes.get(ctx);

    Object value = StringConverter.convert(ctx.getText(), expectedInputType);

    if (value == null) {
      throw new InternalFilterException("The input '" + StringConverter.cleanStringInput(ctx.getText())
          + "' could not be converted to " + expectedInputType);
    }

    return criteriaBuilder.literal(value);

  }

  public Root<?> getRoot() {
    return root;
  }

  public CriteriaQuery<?> getCriteriaQuery() {
    return criteriaQuery;
  }

  public CriteriaBuilder getCriteriaBuilder() {
    return criteriaBuilder;
  }

  public Map<String, Join<?, ?>> getJoins() {
    return joins;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

  private Class<?> getJavaType(Expression<?> expression) {

    if (ExpressionGeneratorParameters.JAVA_TYPE_MODIFIER != null) {
      Class<?> modifiedInputType = ExpressionGeneratorParameters.JAVA_TYPE_MODIFIER.apply(expression);
      if (modifiedInputType != null) {
        return modifiedInputType;
      }
    }

    return expression.getJavaType();

  }

}
