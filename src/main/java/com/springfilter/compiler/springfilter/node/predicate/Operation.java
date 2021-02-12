package com.springfilter.compiler.springfilter.node.predicate;

import com.springfilter.compiler.springfilter.node.IPredicate;
import com.springfilter.compiler.springfilter.token.Operator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Operation implements IPredicate {

  private Operator type;

}
