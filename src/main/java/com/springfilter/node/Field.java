package com.springfilter.node;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.springfilter.Utils;
import com.springfilter.compiler.node.INode;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Field implements IExpression<Object> {

  private String name;

  @Override
  public INode transform(INode parent) {
    return this;
  }

  @Override
  public String generate() {
    return name;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Path<Object> generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {
    return (Path<Object>) Utils.buildDatabasePath(root, joins, name);
  }

}
