package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.EqualOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface EqualStep extends StepWithResult {

  default EqualStepImpl equal(StepWithResult to) {
    return new EqualStepImpl(getOperators(),
        get().infix(
            getOperators().getInfixOperator(EqualOperator.class),
            to.get()));
  }

  class EqualStepImpl extends StepWithResultImpl implements LogicStep {

    EqualStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
