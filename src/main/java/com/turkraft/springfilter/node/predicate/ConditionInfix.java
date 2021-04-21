package com.turkraft.springfilter.node.predicate;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.bson.conversions.Bson;
import com.turkraft.springfilter.FilterConfig;
import com.turkraft.springfilter.exception.InvalidQueryException;
import com.turkraft.springfilter.node.Arguments;
import com.turkraft.springfilter.node.Field;
import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.node.Input;
import com.turkraft.springfilter.token.Comparator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionInfix extends Condition {

  private IExpression left;

  private IExpression right;

  @Override
  public IExpression transform(IExpression parent) {
    left = left.transform(this);
    right = right.transform(this);
    return this;
  }

  @Override
  public String generate() {

    String generatedLeft = left.generate();
    String generatedRight = right.generate();

    if (generatedLeft.isEmpty() || generatedRight.isEmpty())
      return "";

    if (getComparator() == Comparator.IN && generatedRight.equals("()")) { // if right expression
                                                                           // represent arguments
                                                                           // with no argument
      return "";
    }

    return generatedLeft + " " + getComparator().getLiteral() + " " + generatedRight;

  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Predicate generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {

    if (getComparator() == Comparator.IN) { // TODO: 'in' should be a different node
      return inCondition(root, criteriaQuery, criteriaBuilder, joins, payload);
    }

    if (left instanceof Input && right instanceof Input) {
      throw new InvalidQueryException("Left and right expressions of the comparator "
          + getComparator().getLiteral() + " can't be both inputs");
    }

    Expression<?> leftExpression = null;
    Expression<?> rightExpression = null;

    if (getRight() instanceof Input) {
      leftExpression = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins, payload);
      rightExpression = ((Input) getRight()).generate(root, criteriaQuery, criteriaBuilder, joins,
          payload, leftExpression.getJavaType());
    }

    else if (getLeft() instanceof Input) {
      rightExpression = getRight().generate(root, criteriaQuery, criteriaBuilder, joins, payload);
      leftExpression = ((Input) getLeft()).generate(root, criteriaQuery, criteriaBuilder, joins,
          payload, rightExpression.getJavaType());
    }

    else {
      leftExpression = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins, payload);
      rightExpression = getRight().generate(root, criteriaQuery, criteriaBuilder, joins, payload);
    }

    switch (getComparator()) {

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
        if (FilterConfig.ENABLE_ASTERISK_WITH_LIKE_OPERATOR && getRight() instanceof Input) {
          return criteriaBuilder.like(criteriaBuilder.upper((Expression) leftExpression),
              ((Input) getRight()).getValue().getValueAs(String.class).toString().toUpperCase()
                  .replace('*', '%'));
        }
        return criteriaBuilder.like(criteriaBuilder.upper((Expression) leftExpression),
            criteriaBuilder.upper((Expression<String>) rightExpression));
      }

      default:
        throw new InvalidQueryException("Unsupported comparator " + getComparator().getLiteral());

    }

  }

  private Predicate inCondition(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {

    if ((getLeft() instanceof Input)) {
      throw new InvalidQueryException(
          "Left expression of the " + getComparator().getLiteral() + " can't be in an input");
    }

    if (!(getRight() instanceof Arguments)) {
      throw new InvalidQueryException(
          "Right expression of the " + getComparator().getLiteral() + " should be arguments");
    }

    Expression<?> left = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins, payload);

    In<Object> in = criteriaBuilder.in(left);

    for (IExpression argument : ((Arguments) right).getValues()) {

      Expression<?> expression = null;

      if (argument instanceof Input) {
        expression = ((Input) argument).generate(root, criteriaQuery, criteriaBuilder, joins,
            payload, left.getJavaType());
      }

      else {
        expression =
            in.value(argument.generate(root, criteriaQuery, criteriaBuilder, joins, payload));
      }

      in.value(expression);

    }

    return in;

  }

  @Override
  public Bson generateBson(Object payload) {

    if (!(getLeft() instanceof Field)) {
      throw new InvalidQueryException("Left side of infix conditions should be a field");
    }

    if (!(getRight() instanceof Input)) {
      throw new InvalidQueryException("Right side of infix conditions should be an input");
    }

    String key = ((Field) getLeft()).getName();
    Object input = ((Input) getRight()).getValue().getValue();

    switch (getComparator()) {

      case EQUAL:
        return com.mongodb.client.model.Filters.eq(key, input);

      case NOT_EQUAL:
        return com.mongodb.client.model.Filters.ne(key, input);

      case GREATER_THAN:
        return com.mongodb.client.model.Filters.gt(key, input);

      case GREATER_THAN_OR_EQUAL:
        return com.mongodb.client.model.Filters.gte(key, input);

      case LESS_THAN:
        return com.mongodb.client.model.Filters.lt(key, input);

      case LESS_THAN_OR_EQUAL:
        return com.mongodb.client.model.Filters.lte(key, input);

      case LIKE: {
        // return Criteria.where(key).m(input);
      }

      default:
        throw new InvalidQueryException("Unsupported comparator " + getComparator().getLiteral());

    }

  }

}
