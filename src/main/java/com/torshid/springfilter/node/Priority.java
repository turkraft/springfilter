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
public class Priority extends Expression {

  private Expression body;

  @Override
  public Node transform(Node parent) {

    if (parent instanceof Filter || parent instanceof Priority) {
      return body.transform(parent); // no need for priority if parent is root or we have nested priorities
    }

    body = (Expression) body.transform(this);
    return this;

  }

  @Override
  public String generate() {
    return "(" + body.generate() + ")";
  }

  @Override
  public Predicate generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {
    return body.generate(root, criteriaQuery, criteriaBuilder, joins);
  }

}
