package com.turkraft.springfilter.builder;

import com.turkraft.springfilter.builder.AbstractStep.StepWithResultImpl;
import com.turkraft.springfilter.converter.FilterStringConverter;
import com.turkraft.springfilter.definition.FilterOperators;
import com.turkraft.springfilter.language.IsNullOperator;
import com.turkraft.springfilter.parser.node.FilterNode;

public interface IsNullStep extends StepWithResult {

  default IsNullStepImpl isNull() {
    return new IsNullStepImpl(getOperators(), getFilterStringConverter(),
        get().postfix(getOperators().getPostfixOperator(IsNullOperator.class)));
  }

  class IsNullStepImpl extends StepWithResultImpl implements LogicStep {

    IsNullStepImpl(FilterOperators operators, FilterStringConverter filterStringConverter,
        FilterNode result) {
      super(operators, filterStringConverter, result);
    }

  }

}
