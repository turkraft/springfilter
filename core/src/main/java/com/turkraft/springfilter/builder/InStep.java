package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.InOperator;
import com.turkraft.springfilter.parser.node.FilterNode;


public interface InStep extends StepWithResult {

  default InStepImpl in(StepWithResult to) {
    return new InStepImpl(getOperators(), getFilterStringConverter(),
        get().infix(getOperators().getInfixOperator(InOperator.class),
            to.get()));
  }

  class InStepImpl extends StepWithResultImpl implements LogicStep {

    InStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
