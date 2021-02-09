package com.torshid.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import com.torshid.compiler.node.Node;
import com.torshid.compiler.node.Root;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Filter extends Root<Filter> implements IPredicate {

  private Expression body;

  @Override
  public Filter transform(Node parent) {
    body = (Expression) body.transform(this);
    return this;
  }

  @Override
  public String generate() {
    return body.generate();
  }

  @Override
  public Predicate generate(javax.persistence.criteria.Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> joins) {
    return getBody().generate(root, criteriaQuery, criteriaBuilder, joins);
  }

}
