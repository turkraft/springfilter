package com.springfilter.node.predicate;

import com.springfilter.node.IPredicate;
import com.springfilter.token.Comparator;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public abstract class Condition implements IPredicate {

  private Comparator comparator;

}
