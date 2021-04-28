package com.turkraft.springfilter.compiler.node;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Function implements IExpression {

  private String name;

  private Arguments arguments;

  @Override
  public Function transform(IExpression parent) {
    arguments = arguments.transform(this);
    return this;
  }

}
