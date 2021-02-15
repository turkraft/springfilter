package com.turkraft.springfilter.node.predicate;

import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.token.Operator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Operation implements IExpression {

  private Operator operator;

}
