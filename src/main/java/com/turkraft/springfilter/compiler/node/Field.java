package com.turkraft.springfilter.compiler.node;

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

}
