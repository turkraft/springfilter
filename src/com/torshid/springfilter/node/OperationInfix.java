package com.torshid.springfilter.node;

import com.torshid.compiler.node.Node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationInfix extends Operation {

  private Expression left;

  private Expression right;

  @Override
  public Node transform(Node parent) {
    left = (Expression) left.transform(this);
    right = (Expression) right.transform(this);
    return this;
  }

  @Override
  public String generate() {
    return "(" + left.generate() + " " + getType().getLiteral() + " " + right.generate() + ")";
  }

}
