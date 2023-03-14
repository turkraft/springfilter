package com.turkraft.springfilter.helper;

import com.turkraft.springfilter.parser.node.FunctionNode;
import com.turkraft.springfilter.parser.node.OperationNode;

public interface PossibleAggregatedExpression {

  default boolean isAggregated(FunctionNode functionNode) {
    return false;
  }

  default boolean isAggregated(OperationNode operationNode) {
    return false;
  }

}
