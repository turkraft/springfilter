package com.turkraft.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.node.Input;

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

    return generatedLeft + " " + getComparator().getLiteral() + " " + generatedRight;

  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    // crazy stuff going on here

    if (left instanceof Input && right instanceof Input) {
      throw new UnsupportedOperationException(
          "Left and right expressions of the comparator " + getComparator().getLiteral() + " can't be both inputs");
    }

    Expression<?> left = null;
    Expression<?> right = null;

    if (getRight() instanceof Input) {
      left = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins);
      right = ((Input) getRight()).generate(root, criteriaQuery, criteriaBuilder, joins, left.getJavaType());
    }

    else if (getLeft() instanceof Input) {
      right = getRight().generate(root, criteriaQuery, criteriaBuilder, joins);
      left = ((Input) getLeft()).generate(root, criteriaQuery, criteriaBuilder, joins, right.getJavaType());
    }

    else {
      left = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins);
      right = getRight().generate(root, criteriaQuery, criteriaBuilder, joins);
    }

    if (!getComparator().getFieldType().isAssignableFrom(left.getJavaType())
        || !getComparator().getFieldType().isAssignableFrom(right.getJavaType())) {
      throw new UnsupportedOperationException(
          "The comparator " + getComparator().getLiteral() + " only supports type " + getComparator().getFieldType());
    }

    if (!left.getJavaType().equals(right.getJavaType())) {
      // maybe this exception is not needed, JPA already throws an exception
      throw new UnsupportedOperationException(
          "Expressions of different types are not supported in comparator " + getComparator().getLiteral());
    }

    // told u

    switch (getComparator()) {

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
        return criteriaBuilder.lessThanOrEqualTo((Expression) left, (Comparable) right);

      case LIKE: {
        return criteriaBuilder.like(criteriaBuilder.upper((Expression) left),
            criteriaBuilder.upper((Expression<String>) right));
      }

      default:
        throw new UnsupportedOperationException("Unsupported comparator " + getComparator().getLiteral());

    }

  }

}
