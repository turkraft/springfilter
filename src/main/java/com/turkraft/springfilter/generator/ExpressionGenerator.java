package com.turkraft.springfilter.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.springframework.expression.ExpressionException;
import com.turkraft.springfilter.Pair;
import com.turkraft.springfilter.SpringFilterUtils;
import com.turkraft.springfilter.compiler.node.Arguments;
import com.turkraft.springfilter.compiler.node.Field;
import com.turkraft.springfilter.compiler.node.Function;
import com.turkraft.springfilter.compiler.node.FunctionType;
import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.node.Input;
import com.turkraft.springfilter.compiler.node.predicate.ConditionInfix;
import com.turkraft.springfilter.compiler.node.predicate.ConditionPostfix;
import com.turkraft.springfilter.compiler.node.predicate.OperationInfix;
import com.turkraft.springfilter.compiler.node.predicate.OperationPrefix;
import com.turkraft.springfilter.compiler.token.Comparator;
import com.turkraft.springfilter.exception.InvalidQueryException;

public class ExpressionGenerator implements Generator<Expression<?>> {

  public static Expression<?> run(
      IExpression expression,
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {
    return (new ExpressionGenerator(root, criteriaQuery, criteriaBuilder, joins, payload))
        .generate(expression);
  }

  public static Expression<?> run(
      IExpression expression,
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins) {
    return run(expression, root, criteriaQuery, criteriaBuilder, joins, null);
  }

  public static Expression<?> run(
      IExpression expression,
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    return run(expression, root, criteriaQuery, criteriaBuilder, new HashMap<String, Join<?, ?>>());
  }

  private Root<?> root;
  private CriteriaQuery<?> criteriaQuery;
  private CriteriaBuilder criteriaBuilder;
  private Map<String, Join<?, ?>> joins;
  private Object payload;

  public ExpressionGenerator(Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<?, ?>> joins, Object payload) {
    this.root = root;
    this.criteriaQuery = criteriaQuery;
    this.criteriaBuilder = criteriaBuilder;
    this.joins = joins;
    this.payload = payload;
    criteriaQuery.distinct(true);
  }

  public ExpressionGenerator(Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<?, ?>> joins) {
    this(root, criteriaQuery, criteriaBuilder, joins, null);
  }

  public ExpressionGenerator(Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    this(root, criteriaQuery, criteriaBuilder, new HashMap<>());
  }

  @Override
  public Expression<?> generate(Field expression) {
    return ExpressionGeneratorUtils.getDatabasePath(root, joins, payload, expression.getName(),
        ExpressionGeneratorParameters.FILTERING_AUTHORIZATION);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public Expression<?> generate(Function expression) {

    FunctionType type = FunctionType.from(expression.getName());

    if (type == null) {
      throw new InvalidQueryException("The function " + expression.getName() + " does not exist");
    }

    if (!type.hasCustomizedBehavior()) {

      List<Pair<IExpression, Expression<?>>> expressions = new LinkedList<>();

      // the generated expression is directly stored in the list if it's not based on an input,
      // otherwise we will generate it later

      for (IExpression argument : expression.getArguments().getValues()) {
        if (argument instanceof Input) {
          expressions.add(new Pair<IExpression, Expression<?>>(argument, null));
        } else {
          expressions.add(new Pair<IExpression, Expression<?>>(argument, generate(argument)));
        }
      }

      if (!type.matches(expression.getName(), expressions)) {
        throw new InvalidQueryException(
            "Input type given to the function " + expression.getName() + " are incorrect");
      }

      // the following method is used to get the expression of the argument at the given index

      java.util.function.Function<Integer, Expression<?>> getter = (index) -> {
        if (expressions.get(index).getKey() instanceof Input) {
          return generate(((Input) expressions.get(index).getKey()),
              type.isVariadic() ? type.getArgumentTypes()[0].getComponentType()
                  : type.getArgumentTypes()[index]);
        }
        return expressions.get(index).getValue();
      };

      switch (type) {

        case ABSOLUTE:
          return criteriaBuilder.abs((Expression<Number>) getter.apply(0));

        case AVERAGE:
          return criteriaBuilder.avg((Expression<Number>) getter.apply(0));

        case MIN:
          return criteriaBuilder.min((Expression<Number>) getter.apply(0));

        case MAX:
          return criteriaBuilder.max((Expression<Number>) getter.apply(0));

        case SUM:
          return criteriaBuilder.sum((Expression<Number>) getter.apply(0));

        case CURRENTDATE:
          return criteriaBuilder.currentDate();

        case CURRENTTIME:
          return criteriaBuilder.currentTime();

        case CURRENTTIMESTAMP:
          return criteriaBuilder.currentTimestamp();

        case SIZE:
          return criteriaBuilder.size((Expression<Collection>) getter.apply(0));

        case LENGTH:
          return criteriaBuilder.length((Expression<String>) getter.apply(0));

        case TRIM:
          return criteriaBuilder.trim((Expression<String>) getter.apply(0));

        case UPPER:
          return criteriaBuilder.upper((Expression<String>) getter.apply(0));

        case LOWER:
          return criteriaBuilder.lower((Expression<String>) getter.apply(0));

        case CONCAT:
          List<Expression<String>> strings = new ArrayList<Expression<String>>(expressions.size());
          for (Pair<IExpression, Expression<?>> pair : expressions) {
            strings.add((Expression<String>) getter.apply(expressions.indexOf(pair)));
          }
          return SpringFilterUtils.merge(criteriaBuilder::concat, strings);

        default:

      }

    }

    switch (type) {

      // case ALL:
      // return criteriaBuilder.all(getSubquery(expression));
      //
      // case ANY:
      // return criteriaBuilder.any(getSubquery(expression));
      //
      // case SOME:
      // return criteriaBuilder.some(getSubquery(expression));

      case EXISTS:
        return criteriaBuilder.exists(getSubquery(expression));

      default:

    }

    throw new InvalidQueryException("Unsupported function " + type.name().toLowerCase());

  }

  @SuppressWarnings({"unchecked"})
  private Subquery<Integer> getSubquery(Function expression) {

    // /!\ TODO: this method won't work with all/any/some, subquery's select() should be refactored

    if (expression.getArguments().getValues().size() != 1) {
      throw new InvalidQueryException(
          "The function " + expression.getName() + " needs one argument");
    }

    Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);

    Root<?> subroot = subquery.correlate(root);

    Expression<?> predicate = ExpressionGenerator.run(expression.getArguments().getValues().get(0),
        subroot, criteriaQuery, criteriaBuilder, new HashMap<String, Join<?, ?>>());

    if (!Boolean.class.isAssignableFrom(predicate.getJavaType())) {
      throw new InvalidQueryException(
          "The function " + expression.getName() + " needs a predicate as its first argument");
    }

    subquery.select(criteriaBuilder.literal(1));
    subquery.where((Expression<Boolean>) predicate);

    return subquery;

  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Expression<?> generate(ConditionInfix expression) {

    if (expression.getComparator() == Comparator.IN) { // TODO: 'in' should be a different node
      return inCondition(expression);
    }

    if (expression.getLeft() instanceof Input && expression.getRight() instanceof Input) {
      throw new InvalidQueryException("Left and right expressions of the comparator "
          + expression.getComparator().getLiteral() + " can't be both inputs");
    }

    Expression<?> leftExpression = null;
    Expression<?> rightExpression = null;

    if (expression.getRight() instanceof Input) {
      leftExpression = generate(expression.getLeft());
      rightExpression = generate((Input) expression.getRight(), leftExpression.getJavaType());
    }

    else if (expression.getLeft() instanceof Input) {
      rightExpression = generate(expression.getRight());
      leftExpression = generate((Input) expression.getLeft(), rightExpression.getJavaType());
    }

    else {
      leftExpression = generate(expression.getLeft());
      rightExpression = generate(expression.getRight());
    }

    switch (expression.getComparator()) {

      case EQUAL:
        return criteriaBuilder.equal(leftExpression, rightExpression);

      case NOT_EQUAL:
        return criteriaBuilder.notEqual(leftExpression, rightExpression);

      case GREATER_THAN:
        return criteriaBuilder.greaterThan((Expression<? extends Comparable>) leftExpression,
            (Expression<? extends Comparable>) rightExpression);

      case GREATER_THAN_OR_EQUAL:
        return criteriaBuilder.greaterThanOrEqualTo(
            (Expression<? extends Comparable>) leftExpression,
            (Expression<? extends Comparable>) rightExpression);

      case LESS_THAN:
        return criteriaBuilder.lessThan((Expression<? extends Comparable>) leftExpression,
            (Expression<? extends Comparable>) rightExpression);

      case LESS_THAN_OR_EQUAL:
        return criteriaBuilder.lessThanOrEqualTo((Expression<? extends Comparable>) leftExpression,
            (Expression<? extends Comparable>) rightExpression);

      case LIKE: {
        // TODO: factorize
        if (ExpressionGeneratorParameters.CASE_SENSITIVE_LIKE_OPERATOR) {
          if (ExpressionGeneratorParameters.ENABLE_ASTERISK_WITH_LIKE_OPERATOR
              && expression.getRight() instanceof Input) {
            return criteriaBuilder.like((Expression) leftExpression, ((Input) expression.getRight())
                .getValue().getValueAs(String.class).toString().replace('*', '%'));
          }
          return criteriaBuilder.like((Expression) leftExpression,
              (Expression<String>) rightExpression);
        } else {
          if (ExpressionGeneratorParameters.ENABLE_ASTERISK_WITH_LIKE_OPERATOR
              && expression.getRight() instanceof Input) {
            return criteriaBuilder.like(criteriaBuilder.upper((Expression) leftExpression),
                ((Input) expression.getRight()).getValue().getValueAs(String.class).toString()
                    .toUpperCase().replace('*', '%'));
          }
          return criteriaBuilder.like(criteriaBuilder.upper((Expression) leftExpression),
              criteriaBuilder.upper((Expression<String>) rightExpression));
        }
      }

      default:
        throw new InvalidQueryException(
            "Unsupported comparator " + expression.getComparator().getLiteral());

    }

  }

  private Expression<?> inCondition(ConditionInfix expression) {

    if ((expression.getLeft() instanceof Input)) {
      throw new InvalidQueryException("Left expression of the comparator "
          + expression.getComparator().getLiteral() + " can't be an input");
    }

    if (!(expression.getRight() instanceof Arguments)) {
      throw new InvalidQueryException("Right expression of the comparator "
          + expression.getComparator().getLiteral() + " should be arguments");
    }

    Expression<?> left = generate(expression.getLeft());

    In<Object> in = criteriaBuilder.in(left);

    for (IExpression argument : ((Arguments) expression.getRight()).getValues()) {

      Expression<?> argumentExpression = null;

      if (argument instanceof Input) {
        argumentExpression = generate(((Input) argument), left.getJavaType());
      }

      else {
        argumentExpression = in.value(generate(argument));
      }

      in.value(argumentExpression);

    }

    return in;

  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<?> generate(ConditionPostfix expression) {

    Expression<?> leftExpression = generate(expression.getLeft());

    switch (expression.getComparator()) {

      case EMPTY:
      case NULL:
        if (Collection.class.isAssignableFrom(leftExpression.getJavaType())
            && leftExpression instanceof PluralAttributePath) {
          return criteriaBuilder.isEmpty((Expression<Collection<?>>) leftExpression);
        } else {
          return criteriaBuilder.isNull(leftExpression);
        }

      case NOT_EMPTY:
      case NOT_NULL:
        if (Collection.class.isAssignableFrom(leftExpression.getJavaType())
            && leftExpression instanceof PluralAttributePath) {
          return criteriaBuilder.isNotEmpty((Expression<Collection<?>>) leftExpression);
        } else {
          return criteriaBuilder.isNotNull(leftExpression);
        }

      default:
        throw new InvalidQueryException(
            "The comparator " + expression.getComparator().getLiteral() + " is unsupported");

    }

  }

  @Override
  public Expression<?> generate(OperationInfix expression) {

    Expression<?> leftExpression = generate(expression.getLeft());
    Expression<?> rightExpression = generate(expression.getRight());

    if (!leftExpression.getJavaType().equals(Boolean.class)
        || !rightExpression.getJavaType().equals(Boolean.class)) {
      throw new ExpressionException("Left and right side expressions of the infix operator "
          + expression.getOperator().getLiteral() + " should be predicates");
    }

    switch (expression.getOperator()) {

      case AND:
        return criteriaBuilder.and((Predicate) leftExpression, (Predicate) rightExpression);

      case OR:
        return criteriaBuilder.or((Predicate) leftExpression, (Predicate) rightExpression);

      default:
        throw new InvalidQueryException(
            "Unsupported infix operator " + expression.getOperator().getLiteral());

    }

  }

  @Override
  public Expression<?> generate(OperationPrefix expression) {

    Expression<?> rightExpression = generate(expression.getRight());

    if (!rightExpression.getJavaType().equals(Boolean.class)) {
      throw new ExpressionException("Right side expression of the prefix operator "
          + expression.getOperator().getLiteral() + " should be a predicate");
    }

    switch (expression.getOperator()) {

      case NOT:
        return criteriaBuilder.not((Predicate) rightExpression);

      default:
        throw new InvalidQueryException(
            "Unsupported prefix operator " + expression.getOperator().getLiteral());

    }

  }

  @Override
  public Expression<?> generate(Input expression, Class<?> targetClass) {
    return criteriaBuilder.literal(expression.getValue().getValueAs(targetClass));
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

}
