package com.torshid.springfilter.node;

import com.torshid.compiler.node.Node;
import com.torshid.springfilter.token.Comparator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Condition extends Expression {

  private String field;

  private Comparator.Type comparator;

  @Override
  public Node transform(Node parent) {
    return this;
  }

  @Override
  public String generate() {
    return field + comparator.getLiteral();
  }

}
