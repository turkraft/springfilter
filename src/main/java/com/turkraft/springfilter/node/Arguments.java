package com.turkraft.springfilter.node;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import com.turkraft.springfilter.exception.InvalidQueryException;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Arguments implements IExpression {

  private List<IExpression> values;

  @Override
  public Arguments transform(IExpression parent) {
    for (int i = 0; i < values.size(); i++) {
      values.set(i, values.get(i).transform(this));
    }
    return this;
  }

  @Override
  public String generate() {
    if (values == null) {
      return "()";
    }
    return "("
        + String.join(", ", values.stream().map(a -> a.generate()).collect(Collectors.toList()))
        + ")";
  }

  @Override
  public Expression<?> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins, Object payload) {
    throw new InvalidQueryException("Arguments can't be directly generated");
  }

}
