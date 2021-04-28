package com.turkraft.springfilter.compiler.node.predicate;

import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.token.Operator;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Operation implements IExpression {

  private Operator operator;

}
