package com.torshid.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.torshid.compiler.node.INode;
import com.torshid.springfilter.node.IExpression;
import com.torshid.springfilter.node.Input;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionInfix extends Condition {

  private IExpression<?> left;

  private IExpression<?> right;

  @Override
  public INode transform(INode parent) {
    return this;
  }

  @Override
  public String generate() {
    return getLeft().generate() + " " + getComparator().getLiteral() + " " + getRight().generate();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    if (left instanceof Input && right instanceof Input) {
      throw new UnsupportedOperationException(
          "Left and right expressions of the comparator " + getComparator().getLiteral() + " can't be both inputs");
    }

    Expression<?> left = null;
    Expression<?> right = null;

    if (getRight() instanceof Input) {
      left = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins);
      ((Input) getRight()).setTargetClass(left.getJavaType());
      right = getRight().generate(root, criteriaQuery, criteriaBuilder, joins);
    }

    else if (getLeft() instanceof Input) {
      right = getRight().generate(root, criteriaQuery, criteriaBuilder, joins);
      ((Input) getLeft()).setTargetClass(right.getJavaType());
      left = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins);
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

    switch (getComparator()) {

      case EQUAL:
        return criteriaBuilder.equal(left, right);

      case NOT_EQUAL:
        return criteriaBuilder.notEqual(left, right);

      case GREATER_THAN:
        return criteriaBuilder.greaterThan((Expression<? extends Comparable>) left,
            (Expression<? extends Comparable>) right);

      case GREATER_THAN_EQUAL:
        return criteriaBuilder.greaterThanOrEqualTo((Expression<? extends Comparable>) left,
            (Expression<? extends Comparable>) right);

      case LESS_THAN:
        return criteriaBuilder.lessThan((Expression<? extends Comparable>) left,
            (Expression<? extends Comparable>) right);

      case LESS_THAN_EQUAL:
        return criteriaBuilder.lessThanOrEqualTo((Expression) left, (Comparable) right);

      case LIKE: {
        return criteriaBuilder.like(criteriaBuilder.upper((Expression) left),
            "%" + right.toString().trim().toUpperCase() + "%");
      }

      default:
        throw new UnsupportedOperationException("Unsupported comparator " + getComparator().getLiteral());

    }

  }

}
