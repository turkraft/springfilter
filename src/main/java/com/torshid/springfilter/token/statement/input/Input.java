package com.torshid.springfilter.token.statement.input;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import com.torshid.compiler.node.INode;
import com.torshid.compiler.token.IToken;
import com.torshid.springfilter.token.statement.IStatement;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Input<T> implements IToken, IStatement<T> {

  private T value;

  public abstract Object getValueAs(Class<?> klass);

  @Override
  public INode transform(INode parent) {
    return this;
  }

  @Override
  public Expression<T> generate(Root<?> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder,
      Map<String, Join<Object, Object>> joins) {
    return criteriaBuilder.literal(value);
  }

}
