package com.turkraft.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.node.Filter;
import com.turkraft.springfilter.node.IExpression;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Priority implements IExpression {

  private IExpression body;

  @Override
  public INode transform(INode parent) {

    body = (IExpression) body.transform(this);

    if (parent instanceof Filter || parent instanceof Priority || parent instanceof Operation) {
      return body; // unnecessary priority
    }

    return this;

  }

  @Override
  public String generate() {
    return "(" + body.generate() + ")";
  }

  @Override
  public Expression<?> generate(javax.persistence.criteria.Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> joins) {
    return getBody().generate(root, criteriaQuery, criteriaBuilder, joins);
  }

}
