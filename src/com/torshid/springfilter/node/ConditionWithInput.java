package com.torshid.springfilter.node;

import com.torshid.compiler.node.Node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionWithInput extends Condition {

  private Object input;

  @Override
  public Node transform(Node parent) {
    return this;
  }

  @Override
  public String generate() {
    return super.generate() + input;
  }

}
