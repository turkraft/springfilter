package com.torshid.springfilter.node;

import com.torshid.compiler.node.Node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Priority extends Expression {

  private Expression body;

  @Override
  public Node transform(Node parent) {

    if (parent instanceof Filter || parent instanceof Priority) {
      return body.transform(parent); // no need for priority if parent is root or we have nested priorities
    }

    body = (Expression) body.transform(this);
    return this;

  }

  @Override
  public String generate() {
    return "(" + body.generate() + ")";
  }

}
