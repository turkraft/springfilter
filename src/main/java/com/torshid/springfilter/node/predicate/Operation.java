package com.torshid.springfilter.node.predicate;

import com.torshid.springfilter.token.predicate.IPredicate;
import com.torshid.springfilter.token.predicate.Operator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Operation implements IPredicate {

  private Operator type;

}
