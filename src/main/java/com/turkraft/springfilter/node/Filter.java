package com.turkraft.springfilter.node;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import org.bson.conversions.Bson;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Filter implements IExpression {

  private IExpression body;

  @Override
  public Filter transform(IExpression parent) {
    body = body.transform(this);
    return this;
  }

  @Override
  public String generate() {
    return body.generate();
  }

  @Override
  public Predicate generate(
      javax.persistence.criteria.Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {
    return (Predicate) getBody().generate(root, criteriaQuery, criteriaBuilder, joins, payload);
  }

  @Override
  public Predicate generate(
      javax.persistence.criteria.Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins) {
    return generate(root, criteriaQuery, criteriaBuilder, joins, null);
  }

  @Override
  public Predicate generate(
      javax.persistence.criteria.Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Object payload) {
    return generate(root, criteriaQuery, criteriaBuilder, new HashMap<>(), payload);
  }

  @Override
  public Predicate generate(
      javax.persistence.criteria.Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    return generate(root, criteriaQuery, criteriaBuilder, new HashMap<>());
  }

  @Override
  public Bson generateBson(Object payload) {
    return getBody().generateBson(payload);
  }

}
