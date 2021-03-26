package com.turkraft.springfilter.node;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import com.turkraft.springfilter.exception.InvalidQueryException;
import com.turkraft.springfilter.token.input.IInput;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Input implements IExpression {

  private IInput value;

  @Override
  public IExpression transform(IExpression parent) {
    return this;
  }

  @Override
  public String generate() {
    if (value == null) {
      return "";
    }
    String generatedValue = value.generate();
    if (generatedValue == null || generatedValue.isEmpty()) {
      return "";
    }
    return generatedValue;
  }

  @Override
  public Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload) {
    throw new InvalidQueryException(
        "An input can't be directly generated, you need to use the method which specifies the target type");
  }

  public Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins,
      Object payload,
      Class<?> targetClass) {
    return criteriaBuilder.literal(value.getValueAs(targetClass));
  }

}
