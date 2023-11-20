package com.turkraft.springfilter.definition;

import com.turkraft.springfilter.parser.node.FilterNode;
import com.turkraft.springfilter.parser.node.InfixOperationNode;

public abstract class FilterInfixOperator extends FilterOperator {

  public FilterInfixOperator(String[] tokens, int priority) {
    super(tokens, priority);
  }

  public FilterInfixOperator(String token, int priority) {
    super(token, priority);
  }

  public InfixOperationNode toNode(FilterNode left, FilterNode right) {
    return new InfixOperationNode(left, this, right);
  }

}
