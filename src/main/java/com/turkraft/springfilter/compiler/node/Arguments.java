package com.turkraft.springfilter.compiler.node;

import java.util.List;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Arguments implements IExpression {

  private List<IExpression> values;

  @Override
  public Arguments transform(IExpression parent) {
    for (int i = 0; i < values.size(); i++) {
      values.set(i, values.get(i).transform(this));
    }
    return this;
  }

}
