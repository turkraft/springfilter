package com.turkraft.springfilter.compiler.node.predicate;

import com.turkraft.springfilter.compiler.node.IExpression;
import com.turkraft.springfilter.compiler.token.Comparator;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Condition implements IExpression {

  private Comparator comparator;

}
