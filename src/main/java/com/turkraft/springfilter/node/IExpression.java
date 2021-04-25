package com.turkraft.springfilter.node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.bson.conversions.Bson;

public interface IExpression {

  IExpression transform(IExpression parent);

  String generate();

  default <T> T generate(Function<IExpression, T> func) {
    return func.apply(this);
  }

  javax.persistence.criteria.Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload);

  default javax.persistence.criteria.Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins) {
    return generate(root, criteriaQuery, criteriaBuilder, joins, null);
  }

  default javax.persistence.criteria.Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Object payload) {
    return generate(root, criteriaQuery, criteriaBuilder, new HashMap<>(), payload);
  }

  default javax.persistence.criteria.Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    return generate(root, criteriaQuery, criteriaBuilder, new HashMap<>());
  }

  Bson generateBson(Object payload);

  default Bson generateBson() {
    return generateBson(null);
  }

}
