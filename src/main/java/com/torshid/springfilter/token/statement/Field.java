package com.torshid.springfilter.token.statement;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.torshid.compiler.node.INode;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.Utils;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Field implements IToken, IStatement<Object> {

  private String name;

  @Override
  public INode transform(INode parent) {
    return this;
  }

  @Override
  public String generate() {
    return name;
  }

  @Override
  public Expression<Object> generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {
    return (Expression<Object>) Utils.buildDatabasePath(root, joins, name);
  }

}
