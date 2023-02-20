package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.LessThanOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface LessThanStep extends StepWithResult {

  default LessThanStepImpl lessThan(StepWithResult to) {
    return new LessThanStepImpl(getOperators(), getFilterStringConverter(), get()
        .infix(getOperators().getInfixOperator(LessThanOperator.class), to.get()));
  }

  class LessThanStepImpl extends StepWithResultImpl implements LogicStep {

    LessThanStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
