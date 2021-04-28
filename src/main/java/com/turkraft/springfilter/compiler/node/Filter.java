package com.turkraft.springfilter.compiler.node;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Filter implements IExpression {

  private IExpression body;

  @Override
  public Filter transform(IExpression parent) {
    body = body.transform(this);
    return this;
  }

}
