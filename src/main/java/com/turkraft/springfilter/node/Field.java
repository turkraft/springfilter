package com.turkraft.springfilter.node;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import com.turkraft.springfilter.FilterUtils;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Field implements IExpression {

  private String name;

  @Override
  public IExpression transform(IExpression parent) {
    return this;
  }

  @Override
  public String generate() {
    return name;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Path<Object> generate(
      Root<?> root,
      CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder,
      Map<String, Join<?, ?>> joins) {
    return (Path<Object>) FilterUtils.getDatabasePath(root, joins, name);
  }

}
