package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.NotInOperator;
import com.turkraft.springfilter.parser.node.FilterNode;


public interface NotInStep extends StepWithResult {

  default NotInStepImpl notIn(StepWithResult to) {
    return new NotInStepImpl(getOperators(),
        get().infix(getOperators().getInfixOperator(NotInOperator.class),
            to.get()));
  }

  class NotInStepImpl extends StepWithResultImpl implements LogicStep {

    NotInStepImpl(FilterOperators operators,
        FilterNode result) {
      super(operators, result);
    }

  }

}
