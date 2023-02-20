package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.AndOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface AndStep extends StepWithResult {

  default AndStepImpl and(StepWithResult with) {
    return new AndStepImpl(getOperators(), getFilterStringConverter(),
        get().infix(
            getOperators().getInfixOperator(AndOperator.class),
            with.get()));
  }

  class AndStepImpl extends StepWithResultImpl implements LogicStep {

    AndStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
