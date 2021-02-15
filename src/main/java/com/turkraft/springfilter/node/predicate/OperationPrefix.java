package com.turkraft.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    if (!(getRight() instanceof Predicate)) {
      throw new RuntimeException(
          "Right side expression of the prefix operator " + getOperator().getLiteral() + " should be predicates");
    }

    switch (getOperator()) {

      case NOT:
        return criteriaBuilder.not((Predicate) getRight().generate(root, criteriaQuery, criteriaBuilder, joins));

      default:
        throw new UnsupportedOperationException("Unsupported infix operator " + getOperator().getLiteral());

    }

  }

}
