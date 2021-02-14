package com.turkraft.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.turkraft.springfilter.compiler.node.INode;
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
  public INode transform(INode parent) {
    right = (IExpression) right.transform(this);
    return this;
  }

  @Override
  public String generate() {
    return getType().getLiteral() + "(" + right.generate() + ")";
  }

  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    if (!(getRight() instanceof Predicate)) {
      throw new RuntimeException(
          "Right side expression of the prefix operator " + getType().getLiteral() + " should be predicates");
    }

    switch (getType()) {

      case NOT:
        return criteriaBuilder.not((Predicate) getRight().generate(root, criteriaQuery, criteriaBuilder, joins));

      default:
        throw new UnsupportedOperationException("Unsupported infix operator " + getType().getLiteral());

    }

  }

}
