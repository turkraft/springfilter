package com.turkraft.springfilter.node.predicate;

import com.turkraft.springfilter.node.IExpression;
import com.turkraft.springfilter.token.Comparator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Condition implements IExpression {

  private Comparator comparator;

}
