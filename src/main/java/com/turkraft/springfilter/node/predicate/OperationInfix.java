package com.turkraft.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.expression.ExpressionException;

import com.turkraft.springfilter.exception.InvalidQueryException;
import com.turkraft.springfilter.node.IExpression;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationInfix extends Operation {

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

    if (generatedLeft.isEmpty() || generatedRight.isEmpty()) {
      // if op = and, return "" maybe?
      if (generatedLeft.isEmpty())
        return generatedRight;
      else if (generatedRight.isEmpty())
        return generatedLeft;
      else
        return "";
    }

    return "(" + generatedLeft + " " + getOperator().getLiteral() + " " + generatedRight + ")";

  }

  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    Expression<?> leftExpression = getLeft().generate(root, criteriaQuery, criteriaBuilder, joins);
    Expression<?> rightExpression = getRight().generate(root, criteriaQuery, criteriaBuilder, joins);

    if (!leftExpression.getJavaType().equals(Boolean.class) || !rightExpression.getJavaType().equals(Boolean.class)) {
      throw new ExpressionException("Left and right side expressions of the infix operator "
          + getOperator().getLiteral() + " should be predicates");
    }

    switch (getOperator()) {

      case AND:
        return criteriaBuilder.and((Predicate) leftExpression, (Predicate) rightExpression);

      case OR:
        return criteriaBuilder.or((Predicate) leftExpression, (Predicate) rightExpression);

      default:
        throw new InvalidQueryException("Unsupported infix operator " + getOperator().getLiteral());

    }

  }

}
