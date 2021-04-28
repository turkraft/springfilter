package com.turkraft.springfilter.compiler.node;

import com.turkraft.springfilter.compiler.token.input.IInput;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Input implements IExpression {

  private IInput value;

  @Override
  public IExpression transform(IExpression parent) {
    return this;
  }

}
