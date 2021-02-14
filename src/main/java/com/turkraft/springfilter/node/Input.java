package com.turkraft.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.turkraft.springfilter.token.IToken;
import com.turkraft.springfilter.token.input.IInput;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Input implements IToken, IExpression {

  private IInput value;

  private Class<?> targetClass;

  @Override
  public IExpression transform(IExpression parent) {
    return this;
  }

  @Override
  public String generate() {
    return value.toStringAs(targetClass);
  }

  @Override
  public Expression<?> generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {
    return criteriaBuilder.literal(value.getValueAs(targetClass));
  }

}
