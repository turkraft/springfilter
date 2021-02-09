package com.torshid.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.torshid.compiler.node.Node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationInfix extends Operation {

  private Expression left;

  private Expression right;

  @Override
  public Node transform(Node parent) {
    left = (Expression) left.transform(this);
    right = (Expression) right.transform(this);
    return this;
  }

  @Override
  public String generate() {
    return "(" + left.generate() + " " + getType().getLiteral() + " " + right.generate() + ")";
  }

  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    switch (getType()) {

      case AND:
        return criteriaBuilder.and(getLeft().generate(root, criteriaQuery, criteriaBuilder, joins),
            getRight().generate(root, criteriaQuery, criteriaBuilder, joins));

      case OR:
        return criteriaBuilder.or(getLeft().generate(root, criteriaQuery, criteriaBuilder, joins),
            getRight().generate(root, criteriaQuery, criteriaBuilder, joins));

      default:
        throw new UnsupportedOperationException("Unsupported infix operator " + getType().getLiteral());

    }

  }

}
