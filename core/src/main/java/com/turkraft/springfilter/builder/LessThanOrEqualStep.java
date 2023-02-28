package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.LessThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface LessThanOrEqualStep extends StepWithResult {

  default LessThanOrEqualStepImpl lessThanOrEqual(StepWithResult to) {
    return new LessThanOrEqualStepImpl(getOperators(), get().infix(
        getOperators().getInfixOperator(LessThanOrEqualOperator.class), to.get()));
  }

  class LessThanOrEqualStepImpl extends StepWithResultImpl implements LogicStep {

    LessThanOrEqualStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
