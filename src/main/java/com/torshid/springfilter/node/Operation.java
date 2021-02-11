package com.torshid.springfilter.node;

import com.torshid.springfilter.token.Operator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Operation extends Expression {

  private Operator type;

}
