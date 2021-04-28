package com.turkraft.springfilter.compiler.node.predicate;

import com.turkraft.springfilter.compiler.node.Filter;
import com.turkraft.springfilter.compiler.node.IExpression;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class Priority implements IExpression {

  private IExpression body;

  @Override
  public IExpression transform(IExpression parent) {

    body = body.transform(this);

    if (parent instanceof Filter || parent instanceof Priority || parent instanceof Operation) {
      return body; // unnecessary priority
    }

    return this;

  }

}
