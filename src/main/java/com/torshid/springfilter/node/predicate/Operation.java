package com.torshid.springfilter.node.predicate;

import com.torshid.springfilter.node.IPredicate;
import com.torshid.springfilter.token.Operator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Operation implements IPredicate {

  private Operator type;

}
