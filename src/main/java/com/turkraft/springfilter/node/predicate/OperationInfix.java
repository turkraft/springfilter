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
public class OperationInfix extends Operation {

  private IExpression left;

  private IExpression right;

  @Override
  public IExpression transform(IExpression parent) {
    left = (IExpression) left.transform(this);
    right = (IExpression) right.transform(this);
    return this;
  }

  @Override
  public String generate() {
    return "(" + left.generate() + " " + getType().getLiteral() + " " + right.generate() + ")";
  }

  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    if (!(getLeft() instanceof Predicate && !(getRight() instanceof Predicate))) {
      throw new RuntimeException(
          "Left and right side expressions of the infix operator " + getType().getLiteral() + " should be predicates");
    }

    switch (getType()) {

      case AND:
        return criteriaBuilder.and((Predicate) getLeft().generate(root, criteriaQuery, criteriaBuilder, joins),
            (Predicate) getRight().generate(root, criteriaQuery, criteriaBuilder, joins));

      case OR:
        return criteriaBuilder.or((Predicate) getLeft().generate(root, criteriaQuery, criteriaBuilder, joins),
            (Predicate) getRight().generate(root, criteriaQuery, criteriaBuilder, joins));

      default:
        throw new UnsupportedOperationException("Unsupported infix operator " + getType().getLiteral());

    }

  }

}
