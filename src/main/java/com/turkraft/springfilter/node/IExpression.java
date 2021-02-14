package com.turkraft.springfilter.node;

import java.util.Map;
import java.util.function.Function;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

public interface IExpression {

  IExpression transform(IExpression parent);

  String generate();

  default <T> T generate(Function<IExpression, T> func) {
    return func.apply(this);
  }

  javax.persistence.criteria.Expression<?> generate(Root<?> root, CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> joins);

}
