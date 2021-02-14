package com.turkraft.springfilter.node;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.turkraft.springfilter.compiler.node.INode;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Function implements IExpression {

  private String name;

  private List<IExpression> arguments;

  @Override
  public INode transform(INode parent) {
    for (int i = 0; i < arguments.size(); i++) {
      arguments.add((IExpression) arguments.remove(i).transform(this));
    }
    return this;
  }

  @Override
  public String generate() {
    return name + "(" + String.join(", ", arguments.stream().map(a -> a.generate()).collect(Collectors.toList())) + ")";
  }

  @Override
  public Expression<?> generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {

    if (name.equalsIgnoreCase("count")) {
      return criteriaBuilder.count(arguments.get(0).generate(root, criteriaQuery, criteriaBuilder, joins));
    }

    return null;

  }

}
