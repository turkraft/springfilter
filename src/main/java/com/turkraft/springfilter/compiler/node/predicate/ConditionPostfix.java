package com.turkraft.springfilter.compiler.node.predicate;

import com.turkraft.springfilter.compiler.node.IExpression;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionPostfix extends Condition {

  private IExpression left;

  @Override
  public IExpression transform(IExpression parent) {
    left = left.transform(this);
    return this;
  }

}
