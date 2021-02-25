package com.turkraft.springfilter.node.predicate;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;

import com.turkraft.springfilter.node.Filter;
import com.turkraft.springfilter.node.IExpression;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Priority implements IExpression {

  private IExpression body;

  @Override
  public IExpression transform(IExpression parent) {

    body = body.transform(this);

    if (parent instanceof Filter || parent instanceof Priority || parent instanceof Operation) {
      return body; // unnecessary priority
    }

    return this;

  }

  @Override
  public String generate() {
    String generatedBody = body.generate();
    if (generatedBody.isEmpty())
      return "";
    return "(" + generatedBody + ")";
  }

  @Override
  public Expression<?> generate(
      javax.persistence.criteria.Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {
    return getBody().generate(root, criteriaQuery, criteriaBuilder, joins);
  }

}
