package com.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.springfilter.compiler.node.INode;
import com.springfilter.node.IPredicate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationInfix extends Operation {

  private IPredicate left;

  private IPredicate right;

  @Override
  public INode transform(INode parent) {
    left = (IPredicate) left.transform(this);
    right = (IPredicate) right.transform(this);
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
