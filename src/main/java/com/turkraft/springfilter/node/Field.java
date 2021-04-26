package com.turkraft.springfilter.node;

import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.bson.conversions.Bson;
import com.turkraft.springfilter.FilterConfig;
import com.turkraft.springfilter.PathUtils;
import com.turkraft.springfilter.exception.InvalidQueryException;
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
      Map<String, Join<?, ?>> joins,
      Object payload) {
    return (Path<Object>) PathUtils.getDatabasePath(root, joins, payload, name,
        FilterConfig.FILTERING_AUTHORIZATION);
  }

  @Override
  public Bson generateBson(Object payload) {
    throw new InvalidQueryException("A field can't be directly generated");
  }

}
