package com.torshid.springfilter.node;

import com.torshid.springfilter.token.Operator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Operation implements IPredicate {

  private Operator type;

}
