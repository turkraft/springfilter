package com.turkraft.springfilter.node;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import com.turkraft.springfilter.compiler.node.INode;
import com.turkraft.springfilter.compiler.node.IRoot;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Filter implements IRoot<Filter>, IExpression {

  private IExpression body;

  @Override
  public Filter transform(INode parent) {
    body = (IExpression) body.transform(this);
    return this;
  }

  @Override
  public String generate() {
    return body.generate();
  }

  @Override
  public Predicate generate(javax.persistence.criteria.Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> joins) {
    return (Predicate) getBody().generate(root, criteriaQuery, criteriaBuilder, joins);
  }

  public Predicate generate(javax.persistence.criteria.Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    return generate(root, criteriaQuery, criteriaBuilder, new HashMap<>());
  }

}
