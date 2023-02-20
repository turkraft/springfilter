package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.NotOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface NotStep extends StepWithResult {

  default NotStepImpl not() {
    return new NotStepImpl(getOperators(), getFilterStringConverter(),
        get().prefix(
            getOperators().getPrefixOperator(NotOperator.class)));
  }

  class NotStepImpl extends StepWithResultImpl implements LogicStep {

    NotStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
