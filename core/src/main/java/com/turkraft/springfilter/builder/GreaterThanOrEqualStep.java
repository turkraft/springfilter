package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.GreaterThanOrEqualOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface GreaterThanOrEqualStep extends StepWithResult {

  default GreaterThanOrEqualStepImpl greaterThanOrEqual(StepWithResult to) {
    return new GreaterThanOrEqualStepImpl(getOperators(), get().infix(
        getOperators().getInfixOperator(GreaterThanOrEqualOperator.class), to.get()));
  }

  class GreaterThanOrEqualStepImpl extends StepWithResultImpl implements LogicStep {

    GreaterThanOrEqualStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
