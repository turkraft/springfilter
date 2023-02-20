package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.OrOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface OrStep extends StepWithResult {

  default OrStepImpl or(StepWithResult with) {
    return new OrStepImpl(getOperators(), getFilterStringConverter(),
        get().infix(getOperators().getInfixOperator(OrOperator.class),
            with.get()));
  }

  class OrStepImpl extends StepWithResultImpl implements LogicStep {

    OrStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
