package com.turkraft.springfilter.node.predicate;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.bson.conversions.Bson;
import org.springframework.expression.ExpressionException;
import com.turkraft.springfilter.exception.InvalidQueryException;
import com.turkraft.springfilter.node.IExpression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationPrefix extends Operation {

  private IExpression right;

  @Override
  public IExpression transform(IExpression parent) {
    right = right.transform(this);
    return this;
  }

  @Override
  public String generate() {
    String generatedRight = right.generate();
    if (generatedRight.isEmpty())
      return "";
    return getOperator().getLiteral() + "(" + generatedRight + ")";
  }

  @Override
  public Predicate generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {

    Expression<?> rightExpression =
        getRight().generate(root, criteriaQuery, criteriaBuilder, joins, payload);

    if (!rightExpression.getJavaType().equals(Boolean.class)) {
      throw new ExpressionException("Right side expression of the prefix operator "
          + getOperator().getLiteral() + " should be a predicate");
    }

    switch (getOperator()) {

      case NOT:
        return criteriaBuilder.not((Predicate) rightExpression);

      default:
        throw new InvalidQueryException(
            "Unsupported prefix operator " + getOperator().getLiteral());

    }

  }

  @Override
  public Bson generateBson(Object payload) {

    switch (getOperator()) {

      case NOT:
        return com.mongodb.client.model.Filters.not(getRight().generateBson(payload));

      default:
        throw new InvalidQueryException(
            "Unsupported prefix operator " + getOperator().getLiteral());

    }

  }

}
