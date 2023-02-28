package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.GreaterThanOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface GreaterThanStep extends StepWithResult {

  default GreaterThanStepImpl greaterThan(StepWithResult to) {
    return new GreaterThanStepImpl(getOperators(), get()
        .infix(getOperators().getInfixOperator(GreaterThanOperator.class), to.get()));
  }

  class GreaterThanStepImpl extends StepWithResultImpl implements LogicStep {

    GreaterThanStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
