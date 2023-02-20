package com.turkraft.springfilter.parser.node;

import com.turkraft.springfilter.definition.FilterOperator;

public abstract class OperationNode extends FilterNode {

  private final FilterOperator operator;

  public OperationNode(FilterOperator operator) {
    this.operator = operator;
  }

  public FilterOperator getOperator() {
    return operator;
  }

}
