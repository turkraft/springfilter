package com.torshid.springfilter.node.predicate;

import com.torshid.compiler.node.INode;
import com.torshid.springfilter.node.IPredicate;
import com.torshid.springfilter.token.Comparator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Condition implements IPredicate {

  private Comparator comparator;

  @Override
  public INode transform(INode parent) {
    return this;
  }

}
