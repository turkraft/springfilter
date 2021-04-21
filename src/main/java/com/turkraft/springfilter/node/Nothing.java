package com.turkraft.springfilter.node;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.bson.conversions.Bson;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Nothing implements IExpression {

  @Override
  public IExpression transform(IExpression parent) {
    return parent;
  }

  @Override
  public String generate() {
    return "";
  }

  @Override
  public Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {
    return null;
  }

  @Override
  public Bson generateBson(Object payload) {
    return null;
  }

}
