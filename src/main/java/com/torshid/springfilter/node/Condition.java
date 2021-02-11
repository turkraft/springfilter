package com.torshid.springfilter.node;

import com.torshid.compiler.node.INode;
import com.torshid.springfilter.token.Comparator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Condition extends Expression {

  private String field;

  private Comparator comparator;

  @Override
  public INode transform(INode parent) {
    return this;
  }

  @Override
  public String generate() {
    return field + comparator.getLiteral();
  }

}
