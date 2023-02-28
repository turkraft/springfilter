package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.NotEqualOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface NotEqualStep extends StepWithResult {

  default NotEqualStepImpl notEqual(StepWithResult to) {
    return new NotEqualStepImpl(getOperators(), get()
        .infix(getOperators().getInfixOperator(NotEqualOperator.class), to.get()));
  }

  class NotEqualStepImpl extends StepWithResultImpl implements LogicStep {

    NotEqualStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
