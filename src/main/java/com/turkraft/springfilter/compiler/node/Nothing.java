package com.turkraft.springfilter.compiler.node;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Nothing implements IExpression {

  @Override
  public IExpression transform(IExpression parent) {
    return parent;
  }

}
