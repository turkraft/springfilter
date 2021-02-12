package com.springfilter.node.predicate;

import com.springfilter.node.IPredicate;
import com.springfilter.token.Operator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Operation implements IPredicate {

  private Operator type;

}
